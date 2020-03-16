Our project was to implement Hybrid HS with Jaya in which first half of iterations are of Harmony Search and rest of them of Jaya Search in order to optimise RosenBrock Function used in this code.
Some of the Assumptions involved are-

In Rosenbrock Function we considered a = 1 and b = 100

whereas generalised Rosenbrock function is - f(x, y) = (a - x)^2 + b * (y - x^2)^2

Here we have considered upper bound and lower bound to be -5 and +5.

Total number of iterations are 1000.


some of the constants used in Harmony Search have been used as-
1. harmony memory size(hms) = 25(total number of different possible answers)
2. pitch adjusting rate(par) = 0.4
3. harmony memory considering rule(hmcr) = 0.9
4. band width(bw) = 0.2
5. number of iterations(ni) = 500


For more information regarding choice of above constants and harmony search algorithm please refer to scholaroty article(page 3907 - 3915) - K.S. Lee, Z.W. Geem, A new meta-heuristic algorithm for  continuous engineering optimization: harmony search theory  and practice, Comput. Methods Appl. Mech. Engrg. 194  (2005).

Note - Other 3 screenshots in this repository are the results of above code when run on online compiler of gfg.
