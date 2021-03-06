package thebetweenlands.common.capability.foodsickness;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.config.ConfigHandler;

public class FoodSicknessEntityCapability extends EntityCapability<FoodSicknessEntityCapability, IFoodSicknessCapability, EntityPlayer> implements IFoodSicknessCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "food_sickness");
	}

	@Override
	protected Capability<IFoodSicknessCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_FOOD_SICKNESS;
	}

	@Override
	protected Class<IFoodSicknessCapability> getCapabilityClass() {
		return IFoodSicknessCapability.class;
	}

	@Override
	protected FoodSicknessEntityCapability getDefaultCapabilityImplementation() {
		return new FoodSicknessEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	@Override
	public boolean isPersistent(EntityPlayer oldPlayer, EntityPlayer newPlayer, boolean wasDead) {
		return true;
	}



	private Map<Item, Integer> hatredMap = Maps.newHashMap();
	private int lastHatred = 0;

	@Override
	public FoodSickness getLastSickness() {
		return FoodSickness.getSicknessForHatred(this.lastHatred);
	}

	@Override
	public FoodSickness getSickness(ItemFood food) {
		return FoodSickness.getSicknessForHatred(this.getFoodHatred(food));
	}

	@Override
	public void decreaseHatredForAllExcept(ItemFood food, int decrease) {
		if(decrease > 0) {
			Map<Item, Integer> newHatredMap = Maps.newHashMap();
			for (Item key : this.hatredMap.keySet()) {
				if (key != food) {
					newHatredMap.put(key, Math.max(this.hatredMap.get(key) - decrease, 0));
				}
			}
			if(!newHatredMap.isEmpty()) {
				this.hatredMap.putAll(newHatredMap);
				this.markDirty();
			}
		}
	}

	@Override
	public void increaseFoodHatred(ItemFood food, int amount, int decreaseForOthers) {
		if (!ConfigHandler.useFoodSickness)
			return;
		int finalMaxHatred = FoodSickness.VALUES[Math.max(FoodSickness.VALUES.length - 1, 0)].maxHatred;
		if (this.hatredMap.containsKey(food)) {
			int currentAmount = this.hatredMap.get(food);
			this.hatredMap.put(food, Math.max(Math.min(currentAmount + amount, finalMaxHatred), 0));
		} else {
			this.hatredMap.put(food, Math.max(Math.min(amount, finalMaxHatred), 0));
		}
		this.lastHatred = this.hatredMap.get(food);
		this.decreaseHatredForAllExcept(food, decreaseForOthers);
		this.markDirty();
	}

	@Override
	public int getFoodHatred(ItemFood food) {
		if (this.hatredMap.containsKey(food)) {
			return this.hatredMap.get(food);
		}
		return 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for (Map.Entry<Item, Integer> entry : this.hatredMap.entrySet()) {
			NBTTagCompound listCompound = new NBTTagCompound();
			listCompound.setString("Food", entry.getKey().getRegistryName().toString());
			listCompound.setInteger("Level", entry.getValue());
			list.appendTag(listCompound);
		}
		nbt.setTag("HatredMap", list);
		nbt.setInteger("LastHatred", this.lastHatred);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.hatredMap = Maps.newHashMap();
		NBTTagList list = nbt.getTagList("HatredMap", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound listCompound = list.getCompoundTagAt(i);
			Item food = Item.getByNameOrId(listCompound.getString("Food"));
			if(food != null) {
				int level = listCompound.getInteger("Level");
				this.hatredMap.put(food, level);
			}
		}
		this.lastHatred = nbt.getInteger("LastHatred");
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}
