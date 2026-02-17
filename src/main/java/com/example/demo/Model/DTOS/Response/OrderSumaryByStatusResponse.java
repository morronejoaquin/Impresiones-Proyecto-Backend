public class OrderSummaryByStatusResponse {
    private String status;
    private int count;
    private double totalAmount;

    public OrderSummaryByStatusResponse(String status, int count, double totalAmount) {
        this.status = status;
        this.count = count;
        this.totalAmount = totalAmount;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
