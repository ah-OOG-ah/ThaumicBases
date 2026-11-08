package tb.common.block;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import DummyCore.Utils.MathUtils;
import tb.common.tile.TileBrazier;

/// TODO implement lit brazier state
public class BlockBrazier extends BlockContainer {

    public static final BlockProperty<Boolean> STATE = new BooleanBlockProperty() {
        @Override
        public String getName() {
            return "lit";
        }

        @Override
        public boolean hasTrait(BlockPropertyTrait trait) {
            return switch (trait) {
                case SupportsWorld, WorldMutable, SupportsStacks -> true;
                default -> false;
            };
        }

        @Override
        public Boolean getValue(IBlockAccess world, int x, int y, int z) {
            var te = world.getTileEntity(x, y, z);
            if (te instanceof TileBrazier brazier) return brazier.burning;
            return false;
        }

        @Override
        public void setValue(World world, int x, int y, int z, Boolean value) {
            var te = world.getTileEntity(x, y, z);
            if (te instanceof TileBrazier brazier) brazier.burning = value;
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            return false;
        }
    };

    public BlockBrazier() {
        super(Material.rock);
        setHardness(1F);
        setResistance(1F);
    }

    @Override
    public TileEntity createNewTileEntity(World w, int meta) {
        return new TileBrazier();
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
        return 0x1242fd;
    }

    public void randomDisplayTick(World w, int x, int y, int z, Random r) {
        TileEntity tile = w.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileBrazier && ((TileBrazier) tile).burning) {
            w.spawnParticle(
                "flame",
                x + 0.5D + MathUtils.randomDouble(r) / 4,
                y + 0.875D,
                z + 0.5D + MathUtils.randomDouble(r) / 4,
                0,
                0.04D,
                0);
            for (int i = 0; i < 10; ++i) w.spawnParticle(
                "smoke",
                x + 0.5D + MathUtils.randomDouble(r) / 4,
                y + 1D,
                z + 0.5D + MathUtils.randomDouble(r) / 4,
                0,
                r.nextDouble() / 20,
                0);

            w.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "thaumicbases:fire.loop", 0.1F, 0.1F, false);
        }
    }

    public void addCollisionBoxesToList(final World world, final int i, final int j, final int k,
        final AxisAlignedBB axisalignedbb, final List arraylist, final Entity par7Entity) {
        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 1f, 0.9375f);
        super.addCollisionBoxesToList(world, i, j, k, axisalignedbb, arraylist, par7Entity);
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int i, final int j, final int k) {

        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 1f, 0.9375f);
        super.setBlockBoundsBasedOnState(world, i, j, k);
    }
}
