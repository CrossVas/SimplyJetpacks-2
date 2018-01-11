package tonius.simplyjetpacks.crafting;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tonius.simplyjetpacks.CommonProxy;
import tonius.simplyjetpacks.SimplyJetpacks;
import tonius.simplyjetpacks.item.ItemJetpack;
import tonius.simplyjetpacks.setup.ParticleType;
import tonius.simplyjetpacks.util.NBTHelper;

public class UpgradingRecipe extends ShapedOreRecipe {
	private final IEnergyContainerItem resultItem;
	private final int resultMeta;

	private static int j = 0;

	public UpgradingRecipe(ItemStack result, Object... recipe) {
		super(null, result, recipe);
		setRegistryName(SimplyJetpacks.MODID, "upgradeRecipe" + j);
		j++;
		this.resultItem = (IEnergyContainerItem) result.getItem();
		this.resultMeta = result.getItemDamage();
		result.getEnchantmentTagList();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		int addedEnergy = 0;
		ParticleType particleType = null;
		NBTTagCompound tags = null;

		ItemStack slotStack;
		for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
			slotStack = inventoryCrafting.getStackInSlot(i);
			if (slotStack != null && slotStack.getItem() != null) {
				if (slotStack.getItem() instanceof ItemJetpack) {
					tags = (NBTTagCompound) NBTHelper.getDataMap(slotStack).copy();
				}
				if (slotStack.getItem() instanceof IEnergyContainerItem) {
					addedEnergy += ((IEnergyContainerItem) slotStack.getItem()).getEnergyStored(slotStack);
				}
				else if (OreDictionary.containsMatch(false, CommonProxy.oresListParticles, slotStack)) {
					particleType = ParticleType.values()[slotStack.getItemDamage()];
				}
			}
		}

		ItemStack result = new ItemStack((Item) this.resultItem, 1, this.resultMeta);

		if (tags != null) {
			result.setTagCompound(tags);
		}

		NBTHelper.setInt(result, "Energy", Math.min(addedEnergy, this.resultItem.getMaxEnergyStored(result)));

		if (this.resultItem instanceof ItemJetpack && particleType != null) {
			((ItemJetpack) this.resultItem).setParticleType(result, particleType);
		}

		return result;
	}
}