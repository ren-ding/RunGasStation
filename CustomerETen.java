/**
 * The E10 customer (Thread class), which has two action pay and drives.
 * Each action function will call the monitor's action function.
 * 
 * @author RenDing
 * studentID u5111810
 */

import java.util.Random;

public class CustomerETen implements Runnable {
	/**customer ID*/
	private int customerID;
	/**customer type*/
	private boolean standard;
	/**customer current gas units in tank*/
	private int customerGasUnits;
	/** maximum gas amount of customer's tank*/
	private int maxPetrolUnits;
	/**gas station controller*/
	private GasStation gasStationController;
	/**gas station event logger */
	private GasStationEvent gasStationEvent;
	
	/**use to make action decision*/
	protected Random randomGenerator;
	
	/**
	 * CustomerETen constructor 
	 * 
	 * @param customerID			customer ID
	 * @param maxPetrolUnits		maximum size of tank
	 * @param gasStationController	gas station controller(the monitor)
	 * @param gasStationEvent		gas station event logger
	 */
	public CustomerETen(int customerID, int maxPetrolUnits, GasStation gasStationController, GasStationEvent gasStationEvent) {
		this.customerID = customerID;
		this.standard = false;					//E10 customer
		this.customerGasUnits = 0;
		this.maxPetrolUnits = maxPetrolUnits;
		this.gasStationController = gasStationController;
		this.gasStationEvent = gasStationEvent;
	}
	
	/**
	 * pay action, will call the cashier's pay action.
	 * the cashier's pay action will check if there is an available pump and deliver the pump ID to then customer
	 * 
	 * @param howManyUnits				the units of gas the customer will pay
	 * @return							delivered pump ID
	 * @throws InterruptedException
	 */
	public synchronized int payETen(int howManyUnits) throws InterruptedException {
		//an assertion that the paid gas shall less than or equal to tank remaining level
		assert (howManyUnits <= this.maxPetrolUnits-customerGasUnits);
		
		//pay to the cashier, check if there is an available pump
		return this.gasStationController.getCashier().payETen(this.customerID, howManyUnits); 
	}
	
	/**
	 * drives action, just consuming 1 unit of gas in customer's car tank and logging it
	 */
	public synchronized void drives() {
		//an assertion that the tank level is greater than 0
		assert (this.customerGasUnits > 0);
		
		//drives cause gas consuming
		customerGasUnits--;
		this.gasStationEvent.logDrive(customerID);
	}
	
	/**
	 * It randomly make a choice(pay or drives).
	 * If pay, it randomly generate a paid gas units and call pay and gas action
	 */
	public void run() {
		try {
			randomGenerator = new Random();
			while(true) {
				int randomActionChoicce = randomGenerator.nextInt(2);
				//choose to pay
				if(randomActionChoicce == 0) {
					int randomUnits = randomGenerator.nextInt(this.maxPetrolUnits - this.customerGasUnits+1);
					if(randomUnits != 0) {
						//the paid gas shall less than or equal to tank remaining level
						if(randomUnits <= this.maxPetrolUnits-customerGasUnits) {
							int pumpID = this.payETen(randomUnits);
							gasStationEvent.sleepEvents();//for testing
							gasStationController.getPumps()[pumpID].startETen(customerID, randomUnits);
							gasStationEvent.sleepEvents();//for testing
							gasStationController.getPumps()[pumpID].gasETen(customerID, randomUnits,gasStationController);
							gasStationEvent.sleepEvents();//for testing
						}
					}
				} 
				//choose to drive
				if(randomActionChoicce == 1) {
					if(this.customerGasUnits != 0) {
						this.drives();
					}
				}
			}
		} catch(InterruptedException ie) {
			System.out.println("Interrupted Exception in customer "+this.customerID+ "thread! - please check ");
		}
	}
	
}
