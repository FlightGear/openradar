
 Implementation Notes to find around
 
 
 HISTORICAL NOTES
 
 This project was started and driven for a longer time by Ralf Gerlich. He started with the pure radar map, implemented the classes
 that are loading and display the data and the interfaces to FlightGears MultiPlayer protocol. Then he had to stop the work on this
 project.
 In 2012 Martin Spott told Wolfram Wagner about this stalled project. Wolfram started with a few fixes in the existing version, but 
 soon developed his own ideas about how this project should evolve.
 Seeing OpenRadar as more than a pure radar screen brought conflicts with the existing code. So wherever possible, he tried to separate
 the technical from the front end coding. 
 The front end code, especially the Swing parts are in the sub-packages of de.knewcleus.gui, separated in recognizable packages. 
 Additional to the technical data objects of former OpenRadar, there are now new FrontEnd objects, a mix of beans and facades that
 provide data as the front end needs it. They usually start with the prefix GUI. 
 