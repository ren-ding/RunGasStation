/**
 * The main class for assignment 1,
 * GasStation, Cashier, Pump GasStationEvent objects are construct and initialized in this class
 * 
 * @author Ren DING
 * studentID u5111810
 *
 */

public class RunGasStation {
	/**
	 * the main function, the default variables are 2 standard customers(max tank size 2), 2 pumps.
	 * 
	 * @param args 0 maximum gas amount of the tank,
	 * 			   1 the number of customers,
	 * 			   2 the number of pumps.
	 * 			   3 the number of standard customers
	 */
	public static void main(String[] args) {
		int t = (args.length > 0)? Integer.parseInt(args[0]): 2;
		int n = (args.length > 1)? Integer.parseInt(args[1]): 2;
	    int m = (args.length > 2)? Integer.parseInt(args[2]): 2;
	    int ns = (args.length > 3)? Integer.parseInt(args[3]): n;
	    
	    GasStationEvent gasStationEvent = new GasStationEvent(t, n, m, ns);

	    //construct and initialized a cashier
	    Cashier cashier = new Cashier(gasStationEvent);
	    
	    //construct and initialized m pumps
	    Pump[] pumps = new Pump[m];
	    for(int i = 0; i < m; i++) {
	    	pumps[i] = new Pump(i, gasStationEvent);
	    }
	    
	    //construct and initialized gas station controller with cashier and pumps ( the monitor )
	    GasStation gasStationControl = new GasStation(cashier,pumps);
	    
	    //connect the cashier with the gas station
	    cashier.belongToAGasStation(gasStationControl);
	    
	    //construct and initialized n customers (threads)
	    //standard customer (1..NS), and those that always buy E10 petrol (NS+1..N)
	    Thread[] customers = new Thread[n];
	    for(int i = 1; i <= ns; i++) {
	    	customers[i-1] = new Thread(new Customer(i,t,gasStationControl,gasStationEvent));
	    	customers[i-1].start();
	    }
	    
	    for(int i = ns+1; i <= n; i++) {
	    	customers[i-1] = new Thread(new CustomerETen(i,t,gasStationControl,gasStationEvent));
	    	customers[i-1].start();
	    }
	}
}
