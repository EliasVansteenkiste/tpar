package architecture;


public class FourLutSanitizedWiltonUnidirectional extends FourLutSanitizedAbstract {

	public FourLutSanitizedWiltonUnidirectional(int width, int height, int channelWidth, int K, int L) {
		super(width, height, channelWidth, K, L);
	}

	@Override
	protected void generateSwitchBlocks() {
		//Generate switch_blocks, wilton, unidirectional 
		for (int x= 0; x<width+1; x++) {
			for (int y= 0; y<height+1; y++) {
				if (x!=0 && x != width) {
					RouteNode.connect(horizontalChannels[x][y],horizontalChannels[x+1][y],1,0,1,0);//w->e
					RouteNode.connect(horizontalChannels[x+1][y],horizontalChannels[x][y],0,0,1,0);//e->w
				}
				if (y!=0 && y!= height) {
					RouteNode.connect(verticalChannels[x][y],verticalChannels[x][y+1],0,0,1,0);//n->s
					RouteNode.connect(verticalChannels[x][y+1],verticalChannels[x][y],1,0,1,0);//s->n
				}
				if (x!=0 && y!= 0){
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x][y],1,0,-1,0);//w->n
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x][y],0,0,-1,0);//n->w
				}
				if(x!=width && y!=0){
					RouteNode.connect(horizontalChannels[x+1][y],verticalChannels[x][y],0,1,1,-1);//e->n
					RouteNode.connect(verticalChannels[x][y],horizontalChannels[x+1][y],0,1,1,1);//n->e
				}
				if(x !=0 && y != height){
					RouteNode.connect(verticalChannels[x][y+1],horizontalChannels[x][y],1,-1,1,1);//s->w
					RouteNode.connect(horizontalChannels[x][y],verticalChannels[x][y+1],1,-1,1,-1);//w->s
				}
				if(x !=width && y != height){
					RouteNode.connect(horizontalChannels[x+1][y],verticalChannels[x][y+1],0,0,-1,-2);//e->s
					RouteNode.connect(verticalChannels[x][y+1],horizontalChannels[x+1][y],1,0,-1,-2);//s->e
				}
			}	
		}
	}

}
