package main;

import java.util.List;

public class KernelDensityEstimator {
	private List<KernelCenter> kernelCenters; 
	private int dim;
	public double[] h;
	private double cardTotal;
	
	
	public KernelDensityEstimator(List<KernelCenter> kcs, int d, double cardTotal) {
		this.kernelCenters = kcs;
		this.dim = d;
		this.h = new double[dim];
		this.cardTotal = cardTotal;
	}
	
	public void updateBandwidth(double[] x) {
		double[] meanDist = new double[dim];
		for(KernelCenter kc:kernelCenters) {
			for(int i = 0; i<dim; i++) {
				meanDist[i] += kc.card/cardTotal * (Math.abs(x[i] - kc.value[i]));
			}
		}
		
		for(int i = 0; i<dim; i++) {
			if(meanDist[i] <= 0) h[i] = 0.001;
			else h[i] = meanDist[i];
		}
	}
	
	public double getDensity(double[] x){
		double density = 0.0;
		updateBandwidth(x);
		for(KernelCenter kc: kernelCenters) {
			double dimDensity = 1;
			for(int j = 0; j< dim; j++) {
				dimDensity *= GaussianKernel(Math.abs(x[j] - kc.value[j]),h[j]);
			}
			density += (kc.card/cardTotal)*dimDensity;
		}
		
		return density;
	}
	
	
	public double getDensityUp(double[] x, double[] dimLength){
		double density = 0.0;
		for(KernelCenter kc: kernelCenters) {
			double dimDensity = 1;
			for(int j = 0; j< dim; j++) {
				double dist = Math.abs(x[j] - kc.value[j]) - dimLength[j]/2;
				if(dist < 0) dist = Math.abs(x[j] - kc.value[j]);				
				dimDensity *= GaussianKernel(dist, h[j]);
				
			}
			density += (kc.card/cardTotal)*dimDensity;
		}
		
		return density;
	}
	
	public double getDensityLow(double[] x, double[] dimLength){
		double density = 0.0;
		for(KernelCenter kc: kernelCenters) {
			double dimDensity = 1;
			for(int j = 0; j< dim; j++) {
				dimDensity *= GaussianKernel(Math.abs(x[j] - kc.value[j]) + dimLength[j]/2, h[j]);
			}
			density += (kc.card/cardTotal)*dimDensity;
		}
		return density;
	}
	
	
	public static double GaussianKernel(double x, double h) {
		return (1.0/(Math.sqrt(2*Math.PI)*h)) * Math.exp(-0.5 * Math.pow(x, 2) / Math.pow(h, 2));
	}

	
}
