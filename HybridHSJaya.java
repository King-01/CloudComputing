package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Cloudlet2;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm3;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


public class HybridHSJaya{
	
	
	public static int NumberOfVMs = 4;
	public static int NumberOfCloudlets = 10;
	public static int population = 5;
	public static int iterations = 1900; 
	public static ArrayList<Cloudlet2> cloudletList;
	public static ArrayList<Vm3> vmlist;
	public static double worst_cost;
	public static int max_alloc_perVM = (int)Math.ceil((double)NumberOfCloudlets/(double)NumberOfVMs);
	public static int max_size = max_alloc_perVM*NumberOfVMs;
	public static int vmAllocArray[] = new int[max_size];
	public static int tmp_sample_array[] = new int[NumberOfCloudlets]; 
	public static double tmp_taskcost[][] = new double[NumberOfCloudlets][NumberOfVMs];
	public static int sample_population[][] = new int[population][NumberOfCloudlets];
	public static double cost_population[] = new double[population]; 
	public static int best_sample[] = new int[NumberOfCloudlets]; 
	public static int worst_sample[] = new int[NumberOfCloudlets]; 
	public static double best_cost;
	
	public static void main(String[] args) {
		
		Log.printLine("Starting HybridHSJaya...");
		
		try {
			
			int num_user = 1;
			Calendar calendar = Calendar.getInstance();
			CloudSim.init(num_user, calendar, false);
			
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();
			vmlist = new ArrayList<Vm3>();
			
			//VM description
			int vmid = 0;
			int mips = 250;
			long size = 10000; //image size (MB)
			int ram = 512; //vm memory (MB)
			long bw = 1000;
			int pesNumber = 1; //number of cpus
			String vmm = "Xen"; //VMM name
			double procost[] = new double[3];
			
			double PP1[][] = {{0.00, 0.67, 0.24, 0.29, 0.51},	//PC x PC Communication cost
					 		 {0.67, 0.00, 0.22, 0.11, 0.98},
					 		 {0.24, 0.22, 0.00, 0.14, 0.23},
					 		{0.29, 0.22, 0.46, 0.00, 0.85},
					 		{0.51, 0.22, 0.13, 0.98, 0.00}};
			
			for(int i=0;i<NumberOfVMs;i++)
			{
				procost = PP1[i].clone();
				Vm3 vm1 = new Vm3(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared(), procost);
				vmid++;
				vmlist.add(vm1);
			}

			//submit vm list to the broker
			broker.submitVmList(vmlist);
			
			
			
			cloudletList = new ArrayList<Cloudlet2>();
			
			
			// create cloudlets
			int id = 0;
			long length = 40000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();
			double taskaccess[] = new double[3];
			double taskdata[] = new double[2];
			double TP1[][] = {{1.03 ,1.12, 1.25, 1.33, 1.45},  //Task x PC
                    		 {1.27 ,0.97, 1.28, 0.35, 0.36},
                    		 {0.13 ,1.11, 2.11, 1.36, 1.69},
                    		 {1.26 ,1.12, 0.44, 0.33, 0.96},
                    		 {1.89 ,1.14, 1.22, 1.98, 1.45},
                    		 {1.27 ,0.47, 1.38, 1.11, 1.79},
                    		 {0.13 ,1.11, 1.11, 1.09, 0.99},
                    		 {1.26 ,1.62, 0.14, 0.22, 0.97},
                    		 {1.13 ,1.12, 1.25, 0.12, 0.48},
                    		 {1.89 ,1.34, 0.42, 0.14, 0.98}};
			double data1[][] = {{30,30}, {10,10},{10,10},{10,10},{30,60},{30,50},{30,20},{70,60},{30,40},{30,60}};
			
			for(int i=0;i<NumberOfCloudlets;i++)
			{
				taskaccess = TP1[i].clone();
				taskdata = data1[i].clone();
				Cloudlet2 cloudlet = new Cloudlet2(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel,taskaccess,taskdata);
				cloudlet.setUserId(brokerId);
				//add the cloudlets to the list
				cloudletList.add(cloudlet);
				id++;

			}
			
			broker.submitCloudletList(cloudletList); 
			for(int i = 0; i < max_size; i++) {
				vmAllocArray[i] = i/max_alloc_perVM; //load balancing step.
			}
			
			for(int i = 0; i < population; i++) {
				shuffle_array(vmAllocArray);
				for(int j = 0; j < NumberOfCloudlets; j++) {
					sample_population[i][j] = vmAllocArray[j];
				}
			}
			fill_cost(); 
			calculate_best();
			calculate_worst();
			System.out.println("Population Size: " + population);
			System.out.println("Number of iterations: " + iterations/2 + " Of Harmony Search and " + iterations/2 + " Of Jaya");
			System.out.println("Number of Cloudlets: " + NumberOfCloudlets);
			System.out.println("Number of VMs: " + NumberOfVMs);
			System.out.println("\nBest cost initially: " + best_cost);
			System.out.println("Worst cost initially: " + worst_cost);
			
			System.out.println("Starting Harmony Search....");
			for(int i = 0; i < iterations / 2; i++)
			{
				update_populationhs();
				calculate_best();
				calculate_worst();
			}
			System.out.println("Harmony Search Completed!");
			System.out.println("\nBest cost After Harmony Search: " + best_cost);
			System.out.println("Worst cost After Harmony Search: " + worst_cost);
//			for(int i = 0; i < population; i++) {
//				System.out.println(cost_population[i]);
//				
//			}
			System.out.println("Starting Jaya....");
			for(int i = 0; i < iterations / 2; i++) {
				update_populationjaya();
				calculate_best();
				calculate_worst();
			}
			System.out.println("Jaya Completed!");
//			for(int i = 0; i < population; i++) {
//				System.out.println(cost_population[i]);
//				
//			}
			System.out.println("\nBest cost final: " + best_cost);
			System.out.println("Worst cost final: " + worst_cost + "\n");
			
			for(int i = 0; i < NumberOfCloudlets; i++) {
//				System.out.println(i + " " + best_sample[i]);
				broker.bindCloudletToVm(i, best_sample[i]);
			}
			
			CloudSim.startSimulation();
			
			List<Cloudlet> cloudletList = broker.getCloudletReceivedList();
			
			CloudSim.stopSimulation();
			
			printCloudletList(cloudletList);
			
			Log.printLine("Finished");
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
		
		
	}
	
	
	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 10000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips)));
		
		//4. Create Host with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 2048*4; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;


		//in this example, the VMAllocatonPolicy in use is SpaceShared. It means that only one VM
		//is allowed to run on each Pe. As each Host has only one Pe, only one VM can run on each Host.
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList)
    			)
    		); // This is our first machine
		hostId++;
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList)
    			)
    		);
		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

	       DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
	                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}

	}
	
	
	
	
	public static void calculate_best() {
		
		double __best_cost = 99999999999999999.0;
		int best_idx = 0;
		for(int i = 0; i < population; i++) {
			if(cost_population[i] < __best_cost) {
				__best_cost = cost_population[i];
				best_idx = i;
			}
		}
		
		for(int i = 0; i < NumberOfCloudlets; i++) {
			best_sample[i] = sample_population[best_idx][i];
		}
		
//		System.out.println("Best cost " + best_cost);
			
		best_cost = __best_cost;
		
	}
	static void make_valid(int[] arr) {
		int count[] = new int[NumberOfVMs];
		
		for(int i = 0; i < NumberOfVMs; i++)
			count[i] = 0;
		
		for(int i =0; i < NumberOfCloudlets; i++) {
			count[arr[i]]++;
		}
		
		boolean valid = true;
		
		for(int i = 0; i < NumberOfVMs; i++) {
			if(count[i] > max_alloc_perVM) {
				valid = false;
				break;
			}
		}
		
		if(valid)
			return;
		shuffle_array(vmAllocArray);
		for(int j = 0; j < NumberOfCloudlets; j++) {
			arr[j] = vmAllocArray[j];
		}
	}
	
	public static void calculate_cost() {
		
		double exec[] = new double[NumberOfVMs];
		for(int i = 0; i < NumberOfCloudlets; i++) {
			exec[tmp_sample_array[i]] = cloudletList.get(i).accesscost[tmp_sample_array[i]];
		}
		
		double comm_cost[] = new double[NumberOfVMs];
		for(int i = 0; i < NumberOfVMs; i++) {
			double sum = 0;
			for(int j = 0; j < NumberOfVMs; j++)
				sum += vmlist.get(i).commcost[j];
			
			comm_cost[i] = sum;
			
		}
		
		for(int i = 0; i < NumberOfCloudlets; i++) {
			for(int j = 0; j < NumberOfVMs; j++) {
				tmp_taskcost[i][j] = cloudletList.get(i).datasize[0]*comm_cost[j] + exec[j];
			}
		}
		
		
		
	}

	public static void calculate_worst() {
		
		double __worst_cost = 0;
		int best_idx = 0;
		for(int i = 0; i < population; i++) {
			if(cost_population[i] > __worst_cost) {
				__worst_cost = cost_population[i];
				best_idx = i;
			}
		}
		
		for(int i = 0; i < NumberOfCloudlets; i++) {
			worst_sample[i] = sample_population[best_idx][i];
		}
		
//		System.out.println("worst cost " + best_cost);
		worst_cost = __worst_cost;
			
		
		
	}
	
	public static void update_populationjaya() {
		
		Random r = new Random();
		for(int i = 0; i < population; i++) {
			
			
			for(int j = 0; j < NumberOfCloudlets; j++) {
				int cur = sample_population[i][j];
				int best = best_sample[j];
				int worst = worst_sample[j];
				int tmp = (int)(cur + r.nextDouble()*(best - cur) - r.nextDouble()*(worst - cur) + NumberOfVMs)%(NumberOfVMs);
				tmp_sample_array[j] = tmp;
//				System.out.print(" " + tmp);
			}
//			System.out.println("");
			
			//check if new created array is valid or not, if not valid, fill it with a random valid array
			make_valid(tmp_sample_array); 
			
			
			calculate_cost();// calculate task cost based on tmp_sample_array and updates tmp_taskcost;
			double new_cost = calculate_final_cost(); // returns total cost based on tmp_taskcost;
			
			
			
			// if new cost is lower than previous cost then take the new cost and update the sample population;
			
			if(new_cost < cost_population[i]) {
				cost_population[i] = new_cost;
				for(int j = 0; j < NumberOfCloudlets; j++) {
					sample_population[i][j] = tmp_sample_array[j];
				}
			}
			
		}		
	}
	public static void update_populationhs()
	{
		int hms = 25;
		double par = 0.4, hmcr = 0.9, bw = 0.2, lowerbound = -5, upperbound = 5;
		double r1, r2 ,r3 ,r4, var;
		int copy[] = new int[NumberOfCloudlets];
		double cost = worst_cost;
		for(int i = 0; i < NumberOfCloudlets; i++)
		{
			copy[i] = worst_sample[i];
		}
		Random rand = new Random();
		for(int j = 0; j < NumberOfCloudlets ;j++)
		{
			r1 = rand.nextDouble();
			r2 = rand.nextDouble();
			r4 = rand.nextDouble();
			r3 = rand.nextDouble();
			if(r1 < hmcr)
			{
				var = copy[j];
				if(r2 < par)
				{
					if(rand.nextDouble() < rand.nextDouble())
					{
						var = var + r3 * bw;
					}
					else {
						var = var - r3 * bw;
					}
				}
				if(var > upperbound)
				{
					var = upperbound;
				}
				else if(var < lowerbound)
				{
					var = lowerbound;
				}
			}
			else {
				var = lowerbound + r4 * (upperbound - lowerbound);
			}
			int temp = (int)(var + NumberOfVMs) % NumberOfVMs;
			copy[j] = temp;
		}
		make_valid(copy);
		
		for(int i = 0;i < NumberOfCloudlets; i++)
		{
			tmp_sample_array[i] = copy[i];
		}
		calculate_cost();
		double newcost = calculate_final_cost();
		if(newcost < worst_cost)
		{
			for(int i = 0; i < 5; i++)
			{
				int key = 1;
				for(int j = 0; j < NumberOfCloudlets; j++)
				{
					if(sample_population[i][j] != worst_sample[j])
					{
						key = 0;
						break;
					}
				}
				if(key == 1)
				{
					for(int j = 0; j < NumberOfCloudlets; j++)
					{
						sample_population[i][j] = tmp_sample_array[j];
						cost_population[i] = newcost;
					}
					break;
				}
			}
			int idx = 0;
			double mex = cost_population[0];
			for(int i = 0; i < population; i++)
			{
				if(cost_population[i] < mex)
				{
					idx = i;
					mex = cost_population[i];
				}
			}
			worst_cost = cost_population[idx];
			for(int i = 0; i < NumberOfCloudlets; i++)
			{
				worst_sample[i] = sample_population[idx][i];
			}
			mex = cost_population[0];
			idx = 0;
			for(int i = 1; i < NumberOfVMs; i++)
			{
				if(cost_population[i] > mex)
				{
					idx = i;
					mex = cost_population[i];	
				}
			}
			best_cost = cost_population[idx];
			for(int i = 0; i < NumberOfCloudlets; i++)
			{
				best_sample[i] = sample_population[idx][i];
			}
		}
	}

	public static void fill_cost() {
		for(int i = 0; i < population; i++) {
			for(int j = 0; j < NumberOfCloudlets; j++) {
				tmp_sample_array[j] = sample_population[i][j];
			}
			
			// calculate taskcost for the the given answer in tmp_sample_array;
			calculate_cost();
			
			// calculate total cost to fill cost_population;
			cost_population[i] = calculate_final_cost();
			
		}
	}
	
	
	
	public static double calculate_final_cost() {
		double total_cost = 0;
		for(int i = 0; i < NumberOfCloudlets; i++) {
			total_cost += tmp_taskcost[i][tmp_sample_array[i]];
		}
		
		return total_cost;
	}
	
	static void shuffle_array(int[] ar)
	  {
	    // If running on Java 6 or older, use `new Random()` on RHS here
	    Random rnd = ThreadLocalRandom.current();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
}