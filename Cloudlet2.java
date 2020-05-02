package org.cloudbus.cloudsim;

public class Cloudlet2 extends Cloudlet{

	public double accesscost[] = new double[3];
	public double datasize[] = new double[2];
	public Cloudlet2(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize,
			UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam,
			UtilizationModel utilizationModelBw, double access_cost[],double data[]) {
		super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
				utilizationModelRam, utilizationModelBw);
		
		accesscost = access_cost;
		
		datasize = data;
		
		// TODO Auto-generated constructor stub
	}
	

}
