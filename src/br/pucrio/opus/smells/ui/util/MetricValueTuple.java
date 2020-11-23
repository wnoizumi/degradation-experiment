package br.pucrio.opus.smells.ui.util;

import br.pucrio.opus.smells.metrics.MetricName;

public class MetricValueTuple {
	
	private final MetricName metricName;
	private final Double metricValue;
	
	public MetricValueTuple(MetricName metricName, Double metricValue) {
		this.metricName = metricName;
		this.metricValue = metricValue;
	}
	
	public MetricName getMetricName() {
		return metricName;
	}

	public Double getMetricValue() {
		return metricValue;
	}

	@Override
	public String toString() {
		return metricName.getLabel() + " : " + metricValue;
	}
}
