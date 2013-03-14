***ReadMe file for COMP2310/6310 Assignment 2, 2012.8.31

Name:		Ren DING
StudentId:	u5111810

***Disclaimer***: (modify as appropriate)
  The work that I am submitting for this assignment is without significant
  contributions from others (excepting course staff).

***Descriptions of Files in this submission:***

GasStation.lts, RunGasStation.java: required files (see Assignment document)

SimpleSystem.lts: The Simple System ltsa code.
IntermediateSysNoProperty.lts: The intermediate system without FIFO property
IntermediateSysProperty.lts: The intermediate system with FIFO property (2 customers)

RunGasStation.java 	The main class. GasStation, Cashier, Pump GasStationEvent, Customer objects are construct and initialised in this class.
Customer.java		(Thread class)The standard customer, which has two action pay and drives. Each action function will call the monitor's action function.
CustomerETen.java	(Thread class)The E10 customer, which has two action pay and drives. Each action function will call the monitor's action function.
GasStation.java		(The monitor)It contains a cashier, few pumps and four counters(count for the number of pumps in use, the monitors).
GasStationEvent.java	This class provides methods to log  gas station actions.
Cashier.java		The cashier is responsible to reply pay action and deliver an available pump to customer.
Pump.java		the pump is responsible to start and gas to the customer's car.

/javadoc		I comment each class and function according to the javadoc standard. index.html is my main API.

***Design Decisions in Modelling***

Simple system (Two customers, one pump and one cashier) :
According to the description of simple system, the events should be

customer.pay , (choosing the pump, implicitly,because there is just one pump) , pump.start , pump.gas(or could be called customer.gas), customer.drives

The unit of gas should also be considered, therefore, an when condition is used to check if the gas from pump is more than the available tank space.

when(gas != size of tank) pay[a:1..size of tank-gas] …
|when(gas > 0) drives -> CUSTOMER[gas-1]

The process should be
CUSTOMER, CASHIER and PUMP.
However, when I run it
||CUSTOMER_TWO_PUMP_ONE = ( cs[CID]:CUSTOMER || CASHIER || p:PUMP ) /{p.gas / cs.gas}.

the traces are:
cs.1.pay.1
(could be cs.1.gas.1, cs.2.gas.2, p.start.1.1)
…

the problem is after customer pay, customer can got gas directly(cs.1.pay.1 followed by cs.1.gas.1, cs.2.gas.2)

To solve this problem, I add a process to connect the customer.gas and pump.gas. In this way, pump.start process before customer.gas, which will avoid cs.1.pay.1 followed by cs.1.gas.1, cs.2.gas.2.

Actually, I also tried another way (lock the pump), but I throw it away. This is because when I
LOCK = ( acquire-> release -> LOCK ).
PUMP = ( acquire-> start[c:CID][a:A] -> gas[c][a] -> release ->PUMP).
//||LOCK_PUMP = (PUMP || LOCK). 
It will cause redundancy and error, because it will allow starting from pump.acquire without paying. (see SimpleSystem.lts)

-------------------------------------------------

Intermediate system ( N customers, M pumps and one cashier) :
Comparing Simple system, the first difference is M pumps. Therefore, declare pump as p[p:1..M]:PUMP

According to the hint, which is given from assignment description hint:
When there are more than 1 pumps, the start and pay gas actions will also need to include a pump id. The cashier may not start a pump that is already in use, i.e. start any pump still with an outstanding pay action associated with it.

Therefore, replacing p.start with p[1..P].start in CASHIER and replacing p.gas with p[1..P].gas in GETPUMP.(see IntermediateSysNoProperty.lts)

---------

The other is adding FIFO with property.
Quoting the relevant bit from the forum that the lecture said: A FIFO that keeps track of up to 2 cars that have paid will do.
Here I add FIFO for 2 cars. (see IntermediateSysProperty.lts)
The rule is "if two customers paid, the first customer shall gas before second"

range CPRO = 1..2	// Setting 2 customer FIFO property
property	BEGIN = (cs[c1:CPRO].pay[A] -> CASH[c1]),		//first customer pays
		CASH[c1:CPRO] = (cs[c1].gas[A] -> BEGIN			//first customer gases and finishes paying
				|cs[c2:CPRO].pay[A] -> CASH[c1][c2]),	//second customer pays
		CASH[c1:CPRO][c2:CPRO] = (cs[c1].gas[A] -> CASH[c2]).	//if two customers paid, the first customer shall gas before second

||CUSTOMER_C_PUMP_P_FIFO = (CUSTOMER_C_PUMP_P || BEGIN).

The traces are below,
 cs.1.pay.1
 p.1.start.1.1
 cs.2.pay.1
 p.2.start.2.1
 p.1.gas.1.1
 cs.1.gas.1
 p.2.gas.2.1

When p.2.start.2.1, the next event shall only be p.1.gas.1.1 because of the First in first out safety property.

-------------------------------------------------------

Extended System :

For CASHIER and PUMP, I need to add another variable to mark its currently offer gas type.
CASHIER = ( cs[c:CID].pay[a:A][t:1..2] -> p[PID].start[c][a][t:1..2] -> CASHIER).
PUMP = ( start[c:CID][a:A][t:1..2] -> gas[c][a][t:1..2] ->PUMP).

Two types of customer should be defined.
EXTENDED->(req[c:CID].pay[a:A][t:1..2]->EXTENDED)
||EXTENDED_STATION = (standard: EXTENDED || eten : EXTENDED)

For fair, I need to define process to make each type of customer eventually pay and gas.
Therefore, progress should be used

progress EXTENDED_STATION = {pay}

***Design Decisions in Implementation***
  
Thread class: Customer class is the Thread class, which has two action pay and drives. Each action function will call the monitor's action function.
 
Monitor class: The monitor is a counter, which count for the number of pumps in use (or the number of customer paid gas). Pay action will cause the counter increasing, and receive(gas) action will cause the counter decreasing. I create a class called GasStation and add the counter into this class. Therefore, GasStation contains a cashier, few pumps and four counters(count for the number of pumps in use, the monitors).

Other classes: The cashier is responsible to reply pay action and deliver an available pump to customer, the delivered pump is responsible to start and gas to the customer's car,

An internal monitor invariant checking method
assert(standardCounter >=0 && standardCounter <= numOfPumps);

For simple system.
Counter is useless because of only one pump. Random class is used for customer class, which randomly make a choice(pay or drives) in the while loop. It also randomly generate paid gas units. The gas units in tank is not the monitor, it is just a constraint for if customer can pay or not.

Testing as
java RunGasStation 2 2 1

For intermedia System.
customer call pay action, which will be delivered a pumpID if there are available pumps. Otherwise, this customer will wait(). When delivering a pump, the counter will be increased by one,
if( counter >= this.numOfPumps) ) wait();
counter++; 
When receive(gas) action finished, the counter will be decreased by one.
if(counter == 0) wait();
counter--;
notifyAll();

Testing as
java RunGasStation 2 2 2
java RunGasStation 2 4 2


For Extended system part1.
CustomerETen is created, which is similar to the customer class. Each customer has different types. Pump class also add a boolean variable to specify which type of gas is offered now.
The another counter is also added. Therefore, there are two counters now
/**the number of pumps in use for standard customer*/
private int standardCounter;
/**the number of pumps in use for E10 customer*/
private int eTenCounter;

the wait rule is revised when increasing counter
if( (this.standardCounter + this.eTenCounter >= this.numOfPumps) || (this.standardCounter != 0 && !this.standardPetrol) )
wait();

Testing as
java RunGasStation 2 4 2 2

For Extended system part2.
Other two counters are added, (waitStandardCounter and waitETenCounter).
And the rule is added
waitStandardCounter++;
if( (this.standardCounter + this.eTenCounter >= this.numOfPumps) ||
    (this.standardCounter != 0 && !this.standardPetrol) ||
    (waitETenCounter>0 && !isStandardPetrol() ) ) wait();
waitStandardCounter--;
…

Testing as
java RunGasStation 2 4 2 2
or
java RunGasStation 2 4 4 2

***Notable Deficiencies*** 

Although I implemented the extended system in java, I didn't model it with ltsa. However, what I thought has been written above.



***Feedback from undertaking this assignment*** (optional, not marked)
From this assignment, I learnt how to modelling and implementing concurrency problem.
The main technique is to find monitor and threads.
Originally, when I designed this assignment, I treated the gas units as the monitor, which was wrong. After thinking and reading lectures slides and sample code. I found the number of pumps in use is the monitor. Therefore, most confusion were solved. THe liveness and safety another point I learnt and practiced from this assignment. When implemneting, I saw the single line bridge sample code and turnstile sample code, which are really helpful to my understanding of concurrency liveness
