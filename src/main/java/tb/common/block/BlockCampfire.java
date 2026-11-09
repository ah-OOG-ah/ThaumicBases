package tb.common.block;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.IntegerBlockProperty;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import DummyCore.Utils.MathUtils;
import org.joml.Math;
import tb.common.tile.TileBrazier;
import tb.common.tile.TileCampfire;

public class BlockCampfire extends BlockContainer {

    public static final BlockProperty<Integer> STATE = new IntegerBlockProperty() {
        @Override
        public String getName() {
            return "stage";
        }

        @Override
        public boolean hasTrait(BlockPropertyTrait trait) {
            return switch (trait) {
                case SupportsWorld, WorldMutable, SupportsStacks -> true;
                default -> false;
            };
        }

        @Override
        public Integer getValue(IBlockAccess world, int x, int y, int z) {
            var meta = world.getBlockMetadata(x, y, z);
            if (meta > 1) return 2;
            if (meta > 0) return 1;
            return 0;
        }

        @Override
        public void setValue(World world, int x, int y, int z, Integer value) {
            final int BLOCK_UPDATE = 0x1;
            final int SEND_TO_CLIENT = 0x2;
            world.setBlockMetadataWithNotify(
                    x, y, z,
                    Math.clamp(0, 2, value),
                    BLOCK_UPDATE | SEND_TO_CLIENT);
        }

        @Override
        public Integer getValue(ItemStack stack) {
            return 1;
        }
    };

    public BlockCampfire() {
        super(Material.wood);
        setHardness(1F);
        setResistance(1F);
        setStepSound(soundTypeWood);
    }

    @Override
    public TileEntity createNewTileEntity(World w, int meta) {
        return new TileCampfire();
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public void randomDisplayTick(World w, int x, int y, int z, Random r) {
        if (w.getBlockMetadata(x, y, z) > 1) {
            w.spawnParticle(
                "flame",
                x + 0.5D + MathUtils.randomDouble(r) / 4,
                y,
                z + 0.5D + MathUtils.randomDouble(r) / 4,
                0,
                0.04D,
                0);
            for (int i = 0; i < 10; ++i) w.spawnParticle(
                "smoke",
                x + 0.5D + MathUtils.randomDouble(r) / 4,
                y + 0.1D,
                z + 0.5D + MathUtils.randomDouble(r) / 4,
                0,
                r.nextDouble() / 20,
                0);

            w.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "thaumicbases:fire.loop", 0.1F, 0.1F, false);
        }
    }

    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int side, float vecX, float vecY,
        float vecZ) {
        if (w.isRemote) return true;

        TileCampfire fire = (TileCampfire) w.getTileEntity(x, y, z);
        ItemStack stk = p.getCurrentEquippedItem();
        boolean added = fire.addLog(stk);
        if (!added) added = fire.addFuel(stk);

        if (added) {
            p.inventory.decrStackSize(p.inventory.currentItem, 1);
            if (p.openContainer != null) p.openContainer.detectAndSendChanges();
            p.inventory.markDirty();
            return true;
        }

        return false;
    }

    public void addCollisionBoxesToList(final World world, final int i, final int j, final int k,
        final AxisAlignedBB axisalignedbb, final List arraylist, final Entity par7Entity) {
        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.5f, 0.9375f);
        super.addCollisionBoxesToList(world, i, j, k, axisalignedbb, arraylist, par7Entity);
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess world, final int i, final int j, final int k) {

        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.5f, 0.9375f);
        super.setBlockBoundsBasedOnState(world, i, j, k);
    }
}
