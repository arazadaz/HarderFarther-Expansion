package com.mactso.harderfarther.proxy;

import java.util.List;
import net.minecraft.util.math.BlockPos;

public interface IProxy
{	
	public void setGrimCitadelListValues(List<BlockPos> grimList);
	public void setFogColors( int r, int g, int b );
	public void setDifficulty( int h, int g, int t );
	public void setGrimSong(int song);
}
