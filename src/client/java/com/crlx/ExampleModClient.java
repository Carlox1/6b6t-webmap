package com.crlx;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import java.io.IOException;

public class ExampleModClient implements ClientModInitializer {

	public class ModCommandRegister {
		public static void registerCommands() {
			ClientCommandRegistrationCallback.EVENT.register(CommandHandler::register);
		}
	}

	@Override
	public void onInitializeClient() {
		ModCommandRegister.registerCommands();
		ClientChunkEvents.CHUNK_LOAD.register((clientWorld, worldChunk) -> {
			if (!CommandHandler.webmap) return;
			ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
			if (serverInfo == null || !serverInfo.address.contains("6b6t.org")) return;
			if (clientWorld.getDifficulty() != Difficulty.HARD) return;
			if (!clientWorld.getDimension().effects().toString().equals("minecraft:overworld")) return;
			ChunkPos chunkPos = worldChunk.getPos();


			//I don't get coordinates outside -16384 to 16384
			if (chunkPos.x > 1023 || chunkPos.x < -1024 || chunkPos.z > 1023 || chunkPos.z < -1024) return; //Only works in the spawn (-16384 to 16384)



			String playerName = MinecraftClient.getInstance().player.getName().getString();
			StringBuilder str = new StringBuilder();
			str.append(playerName).append("ñ").append(chunkPos.x).append("_").append(chunkPos.z).append("@");

			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					int highestY = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z);

					for (int y = highestY; y >= -64; y--) {
						BlockPos blockPos = new BlockPos(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
						BlockState blockState = clientWorld.getBlockState(blockPos);
						String blockName = blockState.getBlock().toString().replace("Block{minecraft:","").replace("}","");
						VoxelShape collisionSape = blockState.getCollisionShape(clientWorld, blockPos);

						if (blockName.equals("water") || blockName.equals("lava") || blockName.equals("snow") || !blockName.endsWith("_bed") && !blockName.endsWith("glass") && !collisionSape.isEmpty() && collisionSape.getMax(Direction.Axis.X) - collisionSape.getMin(Direction.Axis.X) > 0.75 && collisionSape.getMax(Direction.Axis.Z) - collisionSape.getMin(Direction.Axis.Z) > 0.75) {
							str.append(blockName).append(",").append(y).append(";");
							break;
						}
					}
				}
			}

			new Thread(() -> {
				try {
					HttpPost.main(str.toString(), false);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}).start();

		});
	}
}