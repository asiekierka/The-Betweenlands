package thebetweenlands.common.item.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;

public class ItemRing extends Item implements IEquippable {
	public ItemRing() {
		this.setMaxStackSize(1);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);

		IEquippable.addEquippedPropertyOverrides(this);
	}

	public boolean canBeUsed(ItemStack stack) {
		return stack.getItemDamage() < stack.getMaxDamage();
	}

	protected float getXPConversionRate(ItemStack stack, EntityPlayer player) {
		//1 xp = 5 damage repaired
		return 5.0F;
	}

	public void drainPower(ItemStack stack, Entity entity) {
		if(stack.getItemDamage() < stack.getMaxDamage() && stack.getItem() instanceof ItemRing && ((ItemRing)stack.getItem()).canBeUsed(stack)) {
			stack.setItemDamage(stack.getItemDamage() + 1);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!player.isSneaking()) {
			if(stack.getItemDamage() > 0 && player.experienceTotal > 0) {
				if(!world.isRemote) {
					int repairPerClick = 40;
					float conversion = this.getXPConversionRate(stack, player);
					float requiredRepair = Math.min(repairPerClick, stack.getItemDamage() / conversion);
					stack.setItemDamage(Math.max(0, stack.getItemDamage() - MathHelper.ceil(Math.min(repairPerClick, player.experienceTotal) * conversion)));
					this.removeXp(player, MathHelper.ceil(Math.min(requiredRepair, player.experienceTotal)));
				}

				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}

		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	protected void removeXp(EntityPlayer player, int amount) {
		int newXP = Math.max(player.experienceTotal - amount, 0);
		player.experienceTotal = 0;
		player.experienceLevel = 0;
		player.experience = 0;
		if(newXP > 0) {
			int xpCap = Integer.MAX_VALUE - player.experienceTotal;
			if (newXP > xpCap) {
				newXP = xpCap;
			}
			player.experience += (float)newXP / (float)player.xpBarCap();
			for (player.experienceTotal += newXP; player.experience >= 1.0F; player.experience /= (float)player.xpBarCap()) {
				player.experience = (player.experience - 1.0F) * (float)player.xpBarCap();
				player.experienceLevel += 1;
			}
		}
	}

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.RING;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity target) {
		return stack.getItemDamage() == 0 || player.experienceTotal == 0 || player.isSneaking();
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity target) {
		return target instanceof EntityPlayer;
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity target, IInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { }

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(entity.ticksExisted % 20 == 0) {
			this.drainPower(stack, entity);
		}
	}
}
