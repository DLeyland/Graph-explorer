# Graph-explorer

Graph explorer was produced in order to allow the investigation of [Evolutionary graph theory](http://www.math.harvard.edu/archive/153_fall_04/Additional_reading_material/evolutionary%20graph%20theory.pdf) (EGT) through the use of Monte-
Carlo simulations. It also allows the investigation of [Environmental ](http://www.sciencedirect.com/science/article/pii/S0022519314003919) EGT and [multiple-mutant](http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2957889/)
 EGT. The primary quantities of interest are the fixation probability and the time to fixation. 
 
 
 The entry point to the program is the UserInterface file. The major functionality available here is split into three categories: processes, simulations, and investigations, as well as graph generation which is treated seperately.
 Processes are the most basic level of functionality and must be performed on a specific graph, which you must either load from a file or generate from the graph generation functions
 before you can run a process. Simulations (mostly) also require a pre-generated graph and repeat the relevant process a specified number times before collating the results and providing you with relevant statistics about the fixation probability and time to fixation.
 
 Finally, investigations are the highest level of functionality. Investigations generate a range of graphs of increasing size, the exact properties and numbers of which are controlled by inputted parameters. 
 A simulation is then run on each graph and the results of the simulations for each graph are collated to produces relevant
 statistics about the quantities of interest for the entire range of graphs.
