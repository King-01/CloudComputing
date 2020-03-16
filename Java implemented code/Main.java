import java.io.*;
import java.util.*;

public class Main
{


static double lowerbound=-5,upperbound=5;
static int pop_size=25;
static double arr[][];
static double brr[];
static double arr1[][];
static double brr1[];
static double bestx,besty,worstx,worsty,newx,newy;
static int TOTAL_ITERATIONS=1000;
static int current_iterations=0;

static void initialize()
{
    arr = new double[pop_size][2];
    brr = new double[pop_size];
    Random rand = new Random();
    for(int i = 0; i < 25; i++)
    {
        double x = rand.nextDouble();
        double y = rand.nextDouble();
        x = (lowerbound + (upperbound-lowerbound) * x);
        y = (lowerbound + (upperbound-lowerbound) * y);
        arr[i][0] = x;
        arr[i][1] = y;
        brr[i] = (1 - x)*(1 - x)+ (100 * (y - x) * (y - x));
    }
}


static void arrayupdater()
{
    for(int i = 0; i < 25; i++)
    {
        if(brr1[i] < brr[i])
        {
            arr[i][0] = arr1[i][0];
            arr[i][1] = arr1[i][1];
            brr[i] = brr1[i];
        }
    }
    
}

static void selector()
{
	for(int i=0;i<25;i++)
    {
        if(brr1[i]<brr[i])
        {
            arr[i][0]=arr1[i][0];
            arr[i][1]=arr1[i][1];
            brr[i]=brr1[i];
        }
    }
}
static void harmonysearch()
{
	double min = brr[0], max = brr[0];
	
	for(int i = 0; i < 25; i++)
	{
		if(min > brr[i])
		{
			min = brr[i];
		}
		if(max < brr[i])
		{
			max = brr[i];
		}
	}
	
	double hms = pop_size, hmcr = 0.9, par = 0.4, bw = 0.2, ni;
	
	double r1, r2, r3, r4, oye;
	
	Random rand = new Random();
	
	arr1 = new double[pop_size][2];
	
	brr1 = new double[pop_size];
	
	
	for(int i = 0 ; i < 25; i++)
	{
		for(int j = 0; j < 2; j++)
		{
			r1 = rand.nextDouble();
			r2 = rand.nextDouble();
			r3 = rand.nextDouble();
			r4 = rand.nextDouble();
			
			if(r1 < hmcr)
			{
				oye = arr[i][j];
				if(r2 < par)
				{
					if(rand.nextDouble() < rand.nextDouble())
					{
						oye = oye + r3 * bw;
					} 
					else
					{
						oye = oye - r3 * bw;
					}
				}
				if(oye > upperbound)
				{
					oye = upperbound;
				}
				if(oye < lowerbound)
				{
					oye = lowerbound;
				}
			}
			else
			{
				oye = lowerbound + r4 * (upperbound - lowerbound);
			}
			arr1[i][j] = oye;
		}
		
        brr1[i] = (1 - arr1[i][0])*(1 - arr1[i][0])+ (100 * (arr1[i][1] - arr1[i][0]) * (arr1[i][1] - arr1[i][0]));
	}
}
static void jaya()
{
	double min = brr[0];
    double max = brr[0];
    bestx = arr[0][0];
    besty = arr[0][1];
    worstx = arr[0][0];
    worsty = arr[0][1];
    for(int i = 0; i < 25; i++)
    {
        if(brr[i] < min)
        {
            bestx = arr[i][0];
            besty = arr[i][1];
            min = brr[i];
        }
        if(brr[i] > max)
        {
            max = brr[i];
            worstx = arr[i][0];
            worsty = arr[i][1];
        }
    }
    double ox, oy;
    double r1, r2, r3, r4;
    Random rand = new Random();
    arr1 = new double[pop_size][2];
    brr1 = new double[pop_size];
    for(int i = 0; i < 25; i++)
    {
        ox = arr[i][0];
        oy = arr[i][1];
        r1 = rand.nextDouble();
        r2 = rand.nextDouble();
        r3 = rand.nextDouble();
        r4 = rand.nextDouble();
        arr1[i][0] = ox + r1 * (bestx - Math.abs(ox)) - r2 * (worstx - Math.abs(ox));
        arr1[i][1] = oy + r3 * (besty - Math.abs(oy)) - r4 * (worsty - Math.abs(oy));
        if(arr1[i][0] < lowerbound)
        {
            arr1[i][0] = lowerbound;
        }
        if(arr1[i][0] > upperbound)
        {
        	arr1[i][0] = upperbound;
        }
        if(arr1[i][1] < lowerbound)
        {
            arr1[i][1] = lowerbound;
        }
        if(arr1[i][1] > upperbound)
        {
        	arr1[i][1] = upperbound;
        }
        double x = arr1[i][0];
        double y = arr1[i][1];
        brr1[i] = (1 - x) * (1 - x) + (100 * (y - x) * (y - x));
    }
}
public static void main(String arg[])
{
	Main fir = new Main();
	fir.initialize();
	while(current_iterations < TOTAL_ITERATIONS / 2)
	{
		fir.harmonysearch();
		fir.selector();
		current_iterations++;
	}
    double ansx, ansy, valmin;
    valmin = brr[0];
    ansx = arr[0][0];
    ansy = arr[0][1];
    for(int i = 1; i < 25; i++)
    {
        if(brr[i] < valmin)
        {
            valmin = brr[i];
            ansx = arr[i][0];
            ansy = arr[i][1];
        }
    }
    System.out.println("The minimum value of the Rosenbrock function after applying harmony search on first half iterations is "+valmin+"\n"+"The x-coordinate is " + ansx +" "+"and the y-coordinate is  "+ansy);
	while(current_iterations < TOTAL_ITERATIONS)
	{
		fir.jaya();
		fir.selector();
		current_iterations++;
	}
    valmin = brr[0];
    ansx = arr[0][0];
    ansy = arr[0][1];
    for(int i = 1; i < 25; i++)
    {
        if(brr[i] < valmin)
        {
            valmin = brr[i];
            ansx = arr[i][0];
            ansy = arr[i][1];
        }
    }
    System.out.println("The minimum value of the Rosenbrock function after applying hybrid harmony search and jaya search is "+valmin+"\n"+"The x-coordinate is " + ansx +" "+"and the y-coordinate is  "+ansy);
	
}
}
