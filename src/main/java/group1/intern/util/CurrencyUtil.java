package group1.intern.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

    public static String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formatted = currencyFormat.format(amount);
        return formatted.replace("₫", "").replace(",00", "").trim() + " VND";
    }
}
