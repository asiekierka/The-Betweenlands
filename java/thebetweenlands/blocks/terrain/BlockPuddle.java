package thebetweenlands.blocks.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPuddle extends Block {
	public BlockPuddle() {
		super(Material.ground);
		setHardness(0.1F);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.puddle");
		setBlockTextureName("thebetweenlands:puddle");
		setBlockBounds(0, 0.0F, 0, 1.0F, 0.07F, 1.0F);
		setTickRandomly(true);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rnd) {
		if(!world.isRemote && !EnvironmentEventRegistry.HEAVY_RAIN.isActive()) {
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
		if(blockAccess.getBlock(x, y, z) == this) {
			return false;
		}
		return super.shouldSideBeRendered(blockAccess, x, y, z, side);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return null;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return World.doesBlockHaveSolidTopSurface(world, x, y-1, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return World.doesBlockHaveSolidTopSurface(world, x, y-1, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if(!World.doesBlockHaveSolidTopSurface(world, x, y-1, z)) {
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return ((BlockSwampWater)BLBlockRegistry.swampWater).stillIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;

		for (int xOff = -1; xOff <= 1; ++xOff) {
			for (int yOff = -1; yOff <= 1; ++yOff) {
				int colorMultiplier = blockAccess.getBiomeGenForCoords(x + yOff, z + xOff).getWaterColorMultiplier();
				avgRed += (colorMultiplier & 16711680) >> 16;
			avgGreen += (colorMultiplier & 65280) >> 8;
			avgBlue += colorMultiplier & 255;
			}
		}

		return (avgRed / 9 & 255) << 16 | (avgGreen / 9 & 255) << 8 | avgBlue / 9 & 255;
	}
}