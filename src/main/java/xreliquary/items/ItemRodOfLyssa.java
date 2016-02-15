package xreliquary.items;


import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.client.ItemModelLocations;
import xreliquary.entities.EntityLyssaHook;
import xreliquary.reference.Names;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemRodOfLyssa extends ItemBase {
    public ItemRodOfLyssa() {
        super(Names.rod_of_lyssa);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    /**
     * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
     * hands.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
        if (player.fishEntity != null) {
            return ItemModelLocations.getInstance().getModel(ItemModelLocations.ROD_OF_LYSSA_CAST);
        }
        return ItemModelLocations.getInstance().getModel(ItemModelLocations.ROD_OF_LYSSA);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player)
    {
        if (player.fishEntity != null)
        {
            player.swingItem();
            player.fishEntity.handleHookRetraction();
        }
        else
        {
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(new EntityLyssaHook(world, player));
            }

            player.swingItem();
        }

        return ist;
    }

}
