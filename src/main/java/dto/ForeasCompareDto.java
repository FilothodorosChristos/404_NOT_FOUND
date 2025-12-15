package dto;

public class ForeasCompareDto {

    private int foreasId;
    private String name;

    // Regular budget
    private double regularYear1;
    private double regularYear2;
    private double regularDiff;
    private Double regularPercentChange;

    // Public investment budget
    private double publicInvYear1;
    private double publicInvYear2;
    private double publicInvDiff;
    private Double publicInvPercentChange;

    // Total
    private double totalYear1;
    private double totalYear2;
    private double totalDiff;
    private Double totalPercentChange;

    // Getters and Setters

    public int getForeasId() {
        return foreasId;
    }

    public void setForeasId(int foreasId) {
        this.foreasId = foreasId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Regular budget
    public double getRegularYear1() {
        return regularYear1;
    }

    public void setRegularYear1(double regularYear1) {
        this.regularYear1 = regularYear1;
    }

    public double getRegularYear2() {
        return regularYear2;
    }

    public void setRegularYear2(double regularYear2) {
        this.regularYear2 = regularYear2;
    }

    public double getRegularDiff() {
        return regularDiff;
    }

    public void setRegularDiff(double regularDiff) {
        this.regularDiff = regularDiff;
    }

    public Double getRegularPercentChange() {
        return regularPercentChange;
    }

    public void setRegularPercentChange(Double regularPercentChange) {
        this.regularPercentChange = regularPercentChange;
    }

    // Public investment budget
    public double getPublicInvYear1() {
        return publicInvYear1;
    }

    public void setPublicInvYear1(double publicInvYear1) {
        this.publicInvYear1 = publicInvYear1;
    }

    public double getPublicInvYear2() {
        return publicInvYear2;
    }

    public void setPublicInvYear2(double publicInvYear2) {
        this.publicInvYear2 = publicInvYear2;
    }

    public double getPublicInvDiff() {
        return publicInvDiff;
    }

    public void setPublicInvDiff(double publicInvDiff) {
        this.publicInvDiff = publicInvDiff;
    }

    public Double getPublicInvPercentChange() {
        return publicInvPercentChange;
    }

    public void setPublicInvPercentChange(Double publicInvPercentChange) {
        this.publicInvPercentChange = publicInvPercentChange;
    }

    // Total
    public double getTotalYear1() {
        return totalYear1;
    }

    public void setTotalYear1(double totalYear1) {
        this.totalYear1 = totalYear1;
    }

    public double getTotalYear2() {
        return totalYear2;
    }

    public void setTotalYear2(double totalYear2) {
        this.totalYear2 = totalYear2;
    }

    public double getTotalDiff() {
        return totalDiff;
    }

    public void setTotalDiff(double totalDiff) {
        this.totalDiff = totalDiff;
    }

    public Double getTotalPercentChange() {
        return totalPercentChange;
    }

    public void setTotalPercentChange(Double totalPercentChange) {
        this.totalPercentChange = totalPercentChange;
    }
}
