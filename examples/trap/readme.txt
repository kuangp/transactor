/* Trap.salsa -- Parallel Trapezoidal Rule
 *
 * Input: Optinal. a and b, the left and right endpoints of the integral.n the number of trapezoids.
 * Output:  Estimate of the integral from a to b of f(x)
 *    using the trapezoidal rule and n trapezoids.
 *
 * Algorithm:
 *    1.  Each actor calculates "its" interval of
 *        integration.
 *    2.  Each actor estimates the integral of f(x)
 *        over its interval using the trapezoidal rule.
 *    3a. Each actor sends its results to the main actor.
 *    3b. The main actor sums the calculations received from
 *        the individual actors and prints the result.
 *
 * Note:  f(x) is hardwired.
 *
 */



Running examples.Heat.DistributedHeat
 <1> edit the theater config file. The default file is theatersFile.txt
     Add host name with port number to that file
 <2> Start theaters at each host specifying in theatersFile.txt
 <3> Execute examples.trap.Trap
     java examples.trap.Trap <iteration> <left point> <right point> <# of trapezoids> <# of actors> <nameserver> <theater file>
