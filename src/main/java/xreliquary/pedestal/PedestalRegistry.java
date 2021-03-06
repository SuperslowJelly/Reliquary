package xreliquary.pedestal;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import xreliquary.api.IPedestalItemWrapper;
import xreliquary.reference.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PedestalRegistry {
	private static final PedestalRegistry INSTANCE = new PedestalRegistry();
	private static final Map<LocationKey, BlockPos> positions = new HashMap<>();

	private final Map<Class<? extends Item>, Supplier<? extends IPedestalItemWrapper>> itemWrappers = new HashMap<>();
	private final Map<Class<? extends Block>, Supplier<? extends IPedestalItemWrapper>> blockWrappers = new HashMap<>();

	public static void registerItemWrapper(Class<? extends Item> itemClass, Supplier<? extends IPedestalItemWrapper> wrapperClass) {
		INSTANCE.itemWrappers.put(itemClass, wrapperClass);
	}

	public static void registerItemBlockWrapper(Class<? extends Block> blockClass, Supplier<? extends IPedestalItemWrapper> wrapperClass) {
		INSTANCE.blockWrappers.put(blockClass, wrapperClass);
	}

	public static Optional<IPedestalItemWrapper> getItemWrapper(ItemStack item) {
		for (Class<? extends Item> itemClass : INSTANCE.itemWrappers.keySet()) {
			if (itemClass.isInstance(item.getItem())) {
				return Optional.of(INSTANCE.itemWrappers.get(itemClass).get());
			}
		}

		for (Class<? extends Block> blockClass : INSTANCE.blockWrappers.keySet()) {
			if (item.getItem() instanceof BlockItem && blockClass.isInstance(((BlockItem) item.getItem()).getBlock())) {
				return Optional.of(INSTANCE.blockWrappers.get(blockClass).get());
			}
		}

		return Optional.empty();
	}

	public static void registerPosition(ResourceLocation dimension, BlockPos pos) {
		LocationKey key = new LocationKey(dimension, pos.toLong());
		if (!positions.containsKey(key)) {
			positions.put(key, pos);
		}
	}

	public static void unregisterPosition(ResourceLocation dimension, BlockPos pos) {
		positions.remove(new LocationKey(dimension, pos.toLong()));
	}

	private static void clearPositions() {
		positions.clear();
	}

	public static List<BlockPos> getPositionsInRange(ResourceLocation dimension, BlockPos startPos, int range) {
		return getPositionsInRange(dimension, startPos, range, range, range);
	}

	private static List<BlockPos> getPositionsInRange(ResourceLocation dimension, BlockPos startPos, int xRange, int yRange, int zRange) {
		List<BlockPos> positionsInRange = new ArrayList<>();
		for (Map.Entry<LocationKey, BlockPos> position : positions.entrySet()) {
			if (!position.getKey().getDimension().equals(dimension)) {
				continue;
			}
			BlockPos pos = position.getValue();
			if (pos.getX() < startPos.getX() - xRange || pos.getX() > startPos.getX() + xRange
					|| pos.getY() < startPos.getY() - yRange || pos.getY() > startPos.getY() + yRange
					|| pos.getZ() < startPos.getZ() - zRange || pos.getZ() > startPos.getZ() + zRange) {
				continue;
			}

			positionsInRange.add(pos);
		}
		return positionsInRange;
	}

	@SubscribeEvent
	public void serverStopping(FMLServerStoppedEvent event) {
		PedestalRegistry.clearPositions();
	}

	private static class LocationKey {
		private final ResourceLocation dimension;
		private final long location;

		LocationKey(ResourceLocation dimension, long location) {
			this.dimension = dimension;
			this.location = location;
		}

		@Override
		public int hashCode() {
			return Objects.hash(dimension, location);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof LocationKey)) {
				return false;
			}

			LocationKey key2 = (LocationKey) o;

			return getDimension().equals(key2.getDimension()) && getLocation() == key2.getLocation();
		}

		ResourceLocation getDimension() {
			return dimension;
		}

		public long getLocation() {
			return location;
		}
	}
}
