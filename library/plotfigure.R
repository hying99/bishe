PlotFigure<-function (BP.univ.graph.en=FALSE,BP.univ.graph,golabel,onto="BP",plot.en=FALSE,distance.enable=FALSE,
                      distancenum,root.enable=FALSE,root.node,output.enable=FALSE,write.pic.name)
{
  if(BP.univ.graph.en==TRUE)
  {
    BP.univ.graph <- Build.universal.graph.ontology.down(ontology = onto)
  }
  
  graph.BP.general <- subGraph(golabel, BP.univ.graph)
  if(plot.en==TRUE)
  {
    x11()
    Pretty.plot.graph(graph.BP.general,fontsize=15,fillcolor="transparent",height=6,
                      width=8,color="transparent", fontcolor="black")
  }
  
  if(distance.enable==TRUE)
  {
    classes.depth <- Select.GO.classes.by.distance(graph.BP.general, distance = distancenum, ontology = onto);
    g.depth <- subGraph(classes.depth, graph.BP.general);
    x11();
    Pretty.plot.graph(g.depth,fontsize=12,fillcolor="transparent",height=2,width=3,color="transparent", fontcolor="black");
  }
  if(root.enable==TRUE)
  {
    selected.nodes <- Select.GO.rooted.classes (graph.BP.general, root.nodes=root.node, ontology=onto);
    g.rooted <- subGraph(selected.nodes, graph.BP.general);
    x11();
    Pretty.plot.graph(g.rooted,fontsize=12,fillcolor="yellow",height=0.9,width=1.4,color="transparent", fontcolor="black");
  }
  if(output.enable==TRUE)
  {
    postscript(write.pic.name,onefile=FALSE);
    
    Pretty.plot.graph(graph.BP.general,fontsize=15,fillcolor="transparent",height=6,width=8,color="transparent", fontcolor="black");
    dev.off();
    if(distance.enable==TRUE)
    {
      postscript("Distance.GO.graph.ps",onefile=FALSE);
      Pretty.plot.graph(g.depth,fontsize=12,fillcolor="transparent",height=2,width=3,color="transparent", fontcolor="black");
      dev.off();
    }
    if(root.enable==TRUE)
    {
      postscript("Root.GO.graph.ps",onefile=FALSE);
      Pretty.plot.graph(g.rooted,fontsize=12,fillcolor="yellow",height=0.9,width=1.4,color="transparent", fontcolor="black");
      dev.off();
    }
  }
  return(graph.BP.general)
}
