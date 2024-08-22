package group1.intern.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueInfo {
	private long orderCount;
    private double currentRevenue;
    private double predictedRevenue;
}
