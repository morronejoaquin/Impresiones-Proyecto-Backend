package com.example.demo.Services;

import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.OrderItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@EnableScheduling
public class CleanupService {
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final GoogleDriveService googleDriveService;

    /**
     * TAREA 1: Borra archivos de ítems eliminados lógicamente (deleted = true).
     * Se ejecuta cada día a las 3 AM.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanDeletedItems() {
        List<OrderItemEntity> deletedItems = orderItemRepository.findAllByDeletedTrue();

        for (OrderItemEntity item : deletedItems) {
            try {
                googleDriveService.deleteFile(item.getDriveFileId());
                orderItemRepository.delete(item); // Borrado físico final
            } catch (Exception e) {
                // Loguear error pero seguir con el siguiente
                System.out.println(("Error al procesar limpieza física del item {}: {}"+ item.getId() + e.getMessage()));
            }
        }
    }

    /**
     * TAREA 2: Borra carritos OPEN que fueron creados hace más de 7 días.
     * Se ejecuta todos los domingos a las 4 AM.
     */
    @Scheduled(cron = "0 0 4 * * SUN")
    @Transactional
    public void purgeAbandonedCarts() {
        Instant limitDate = Instant.now().minus(7, ChronoUnit.DAYS);
        List<CartEntity> abandoned = cartRepository
                .findByCartStatusAndLastModifiedAtBefore(CartStatusEnum.OPEN, limitDate);

        for (CartEntity cart : abandoned) {
            // Primero borramos todos los archivos de Drive de ese carrito
            cart.getItems().forEach(item -> googleDriveService.deleteFile(item.getDriveFileId()));
            // Luego borramos el carrito (y por CascadeType.ALL, sus items en DB)
            cartRepository.delete(cart);
        }
    }
}
