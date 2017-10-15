package thebetweenlands.common.entity.projectiles;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.projectile.EntityThrowable;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityElixir extends EntityThrowable {

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityPotion.class, DataSerializers.ITEM_STACK);

    public EntityElixir(World world) {
        super(world);
        this.setItem(new ItemStack(ItemRegistry.ELIXIR));
    }

    public EntityElixir(World world, EntityLivingBase thrower, ItemStack elixir, float strength) {
        super(world, thrower);
        this.setItem(elixir.copy());
        this.motionX *= strength;
        this.motionY *= strength;
        this.motionZ *= strength;
    }

    protected void entityInit()
    {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    public ItemStack getElixirStack() {
        return this.getDataManager().get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
        this.getDataManager().setDirty(ITEM);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            AxisAlignedBB hitBB = this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
            List hitEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, hitBB);
            if (!hitEntities.isEmpty()) {
                Iterator hitEntitiesIT = hitEntities.iterator();
                while (hitEntitiesIT.hasNext()) {
                    EntityLivingBase affectedEntity = (EntityLivingBase)hitEntitiesIT.next();
                    double entityDst = this.getDistanceSq(affectedEntity);
                    if (entityDst < 16.0D) {
                        double modifier = 1.0D - Math.sqrt(entityDst) / 4.0D;
                        if (affectedEntity == result.entityHit) {
                            modifier = 1.0D;
                        }
                        ItemRegistry.ELIXIR.applyEffect(getElixirStack(), affectedEntity, modifier);
                    }
                }
            }
            this.world.playEvent(2002, new BlockPos(this), ItemRegistry.ELIXIR.getColorMultiplier(getElixirStack(), 0));
            this.setDead();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        ItemStack itemstack = new ItemStack(nbt.getCompoundTag("elixir"));

        if (itemstack.isEmpty()) {
            this.setDead();
        } else {
            this.setItem(itemstack);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        ItemStack stack = getElixirStack();
        if (!stack.isEmpty()) {
            nbt.setTag("elixir", stack.writeToNBT(new NBTTagCompound()));
        }
    }
}