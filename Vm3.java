package org.cloudbus.cloudsim;

public class Vm3 extends Vm{
	public double commcost[]= new double[3];

	public Vm3(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
			CloudletScheduler cloudletScheduler, double comm_cost[]) {
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
		// TODO Auto-generated constructor stub
		
		commcost = comm_cost;
		
	}

}