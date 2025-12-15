package dto;

public class CashFlowCompareDto {

    private String name;
    private double amountYear1;
    private double amountYear2;
    private boolean missingInYear1;
    private boolean missingInYear2;

    public CashFlowCompareDto(String name, double amountYear1, double amountYear2,
                                 boolean missingInYear1, boolean missingInYear2) {
        this.name = name;
        this.amountYear1 = amountYear1;
        this.amountYear2 = amountYear2;
        this.missingInYear1 = missingInYear1;
        this.missingInYear2 = missingInYear2;
    }

    public String getName() {
        return name;
    }

    public double getAmountYear1() {
        return amountYear1;
    }

    public double getAmountYear2() {
        return amountYear2;
    }

    public boolean isMissingInYear1() {
        return missingInYear1;
    }

    public boolean isMissingInYear2() {
        return missingInYear2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Όνομα: ").append(name).append(" | ");

        sb.append("Έτος 1: ");
        if (missingInYear1) sb.append("Μη διαθέσιμο");
        else sb.append(amountYear1);

        sb.append(" | Έτος 2: ");
        if (missingInYear2) sb.append("Μη διαθέσιμο");
        else sb.append(amountYear2);

        return sb.toString();
    }
}
