package com.github.fossil.client.gui.debug;

import com.github.fossil.capabilities.ModCapabilities;
import com.github.fossil.entity.PrehistoricSkeleton;
import com.github.fossil.entity.prehistoric.base.DinosaurEgg;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricAnimatable;
import com.github.fossil.entity.prehistoric.base.PrehistoricDebug;
import com.github.fossil.network.debug.C2SDisableAIMessage;
import com.github.fossil.network.debug.C2SDiscardMessage;
import com.github.fossil.network.debug.C2SSlowMessage;
import com.github.fossil.network.debug.C2SStructureMessage;
import com.github.fossil.network.debug.C2STameMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ported from source's {@code DebugScreen} (the dev-only debug HUD's root screen - opened via the debug-screen
 * keybind, see {@code client.ClientModEvents}). Source sent every action to the server through Architectury's
 * {@code MessageHandler.DEBUG_CHANNEL.sendToServer(...)}; target replaced that entirely with NeoForge
 * {@code CustomPacketPayload} records sent via {@code PacketDistributor.sendToServer(...)} - every message class
 * referenced below already exists on target with a matching constructor.
 */
public class DebugScreen extends Screen {
    private static final List<PathInfo> pathTargets = new ArrayList<>();
    public static CycleButton<Boolean> disableAI;
    public static boolean showPaths;
    public static @Nullable Entity entity;
    public static int rulerMode;
    private static PathInfo currentVision;
    private static int tabShift = 0;
    private final List<DebugTab<? extends Entity>> tabs = new ArrayList<>();
    private DebugTab<? extends Entity> currentTab;
    private double speedMod = 0.5;

    //TODO: Embryo, etc helper
    public DebugScreen(@Nullable Entity newEntity) {
        super(Component.literal("Debug Screen"));
        entity = newEntity;
        if (newEntity == null || !newEntity.isAlive()) {
            entity = null;
        }
    }

    @Nullable
    public static Entity getHitResult(Minecraft mc) {
        Entity camera = mc.getCameraEntity();
        Vec3 view = camera.getViewVector(1.0f);
        double range = 30;
        Vec3 end = camera.getEyePosition().add(view.x * range, view.y * range, view.z * range);
        AABB aabb = camera.getBoundingBox().expandTowards(view.scale(range)).inflate(1);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(camera, camera.getEyePosition(), end, aabb,
                Entity::isPickable, range * range);
        return (entityHitResult != null) ? entityHitResult.getEntity() : null;
    }

    public static void showPath(Player player, List<BlockPos> targets, List<BlockState> blocks, boolean below) {
        if (showPaths) {
            for (int i = 0; i < targets.size(); i++) {
                if (below) {
                    pathTargets.add(new PathInfo(targets.get(i), player.level().getBlockState(targets.get(i).below()), true));
                    player.level().setBlock(targets.get(i).below(), blocks.get(i), 3);
                } else {
                    pathTargets.add(new PathInfo(targets.get(i), player.level().getBlockState(targets.get(i)), false));
                    player.level().setBlock(targets.get(i), blocks.get(i), 3);
                }
            }
        }
    }

    public static void showVision(Player player, BlockPos target, BlockState block) {
        if (currentVision != null) {
            player.level().setBlock(currentVision.targetPos, currentVision.blockState, 3);
        }
        currentVision = new PathInfo(target, player.level().getBlockState(target), false);
        player.level().setBlock(target, block, 3);
    }

    public static void clearPaths() {
        for (int i = pathTargets.size() - 1; i >= 0; i--) {
            boolean below = pathTargets.get(i).below;
            Minecraft.getInstance().level.setBlock(below ? pathTargets.get(i).targetPos.below() : pathTargets.get(i).targetPos, pathTargets.get(i).blockState, 3);
        }
        pathTargets.clear();
        if (currentVision != null) {
            Minecraft.getInstance().level.setBlock(currentVision.targetPos, currentVision.blockState, 3);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        tabs.clear();
    }

    @Override
    protected void init() {
        var builder = CycleButton.onOffBuilder();
        if (entity instanceof Prehistoric prehistoric) {
            tabs.add(new InfoTab(this, prehistoric));
        } else if (entity instanceof TamableAnimal || entity instanceof AbstractHorse) {
            addRenderableWidget(Button.builder(Component.literal("Tame"), button -> {
                PacketDistributor.sendToServer(new C2STameMessage(entity.getId()));
            }).bounds(275, 55, 50, 20).build());
        }
        if (entity instanceof PrehistoricSkeleton skeleton) {
            tabs.add(new SkeletonEditTab(this, skeleton));
        }
        if (entity instanceof DinosaurEgg egg) {
            tabs.add(new EggTab(this, egg));
        }
        if (entity instanceof Animal animal && ModCapabilities.hasEmbryo(animal)) {
            tabs.add(new EmbryoTab(this, animal));
        }
        if (entity instanceof Mob mob && entity instanceof PrehistoricDebug prehistoric) {
            builder.withInitialValue(mob.isNoAi());
            disableAI = builder.create(width / 2 - 225, height - 95, 130, 20, Component.literal("Disable AI"), (cycleButton, object) -> {
                PacketDistributor.sendToServer(new C2SDisableAIMessage(entity.getId(), cycleButton.getValue(), (byte) 0));
            });
            this.addRenderableWidget(disableAI);
            builder.withInitialValue(prehistoric.getDebugTag().getBoolean("disableGoalAI"));
            this.addRenderableWidget(builder.create(width / 2 - 225, height - 70, 130, 20, Component.literal("Disable Goal AI"), (cycleButton, object) -> {
                PacketDistributor.sendToServer(new C2SDisableAIMessage(entity.getId(), cycleButton.getValue(), (byte) 1));
            }));
            builder.withInitialValue(prehistoric.getDebugTag().getBoolean("disableMoveAI"));
            this.addRenderableWidget(builder.create(width / 2 - 225, height - 45, 130, 20, Component.literal("Disable Move AI"), (cycleButton, object) -> {
                PacketDistributor.sendToServer(new C2SDisableAIMessage(entity.getId(), cycleButton.getValue(), (byte) 2));
            }));
            builder.withInitialValue(prehistoric.getDebugTag().getBoolean("disableLookAI"));
            this.addRenderableWidget(builder.create(width / 2 - 225, height - 22, 130, 20, Component.literal("Disable Look AI"), (cycleButton, object) -> {
                PacketDistributor.sendToServer(new C2SDisableAIMessage(entity.getId(), cycleButton.getValue(), (byte) 3));
            }));
        }
        if (entity instanceof Mob mob && mob instanceof PrehistoricAnimatable<?>) {
            tabs.add(new AnimationTab(this, mob));
        }
        if (entity instanceof Prehistoric prehistoric) {
            tabs.add(new InstructionTab(this, prehistoric));
        }
        addRenderableWidget(Button.builder(Component.literal("Ruler"), button -> {
                    rulerMode = 1;
                    onClose();
                    if (currentTab != null) currentTab.onClose();
                })
                .bounds(width / 2 - 91, height - 95, 91, 20)
                .tooltip(Tooltip.create(Component.literal("Measure length. Left click for 1st pos. Right click for 2nd pos")))
                .build());
        addRenderableWidget(Button.builder(Component.literal("Discard All"), button -> {
                    PacketDistributor.sendToServer(new C2SDiscardMessage(-1));
                    if (currentTab != null) currentTab.onClose();
                })
                .bounds(width / 2 - 91, height - 70, 91, 20)
                .tooltip(Tooltip.create(Component.literal("Kills all non-player entities")))
                .build());
        if (entity != null) {
            addRenderableWidget(Button.builder(Component.literal("Discard This"), button -> {
                PacketDistributor.sendToServer(new C2SDiscardMessage(entity.getId()));
                if (currentTab != null) currentTab.onClose();
            }).bounds(width / 2, height - 70, 91, 20).build());
        }
        addRenderableWidget(Button.builder(Component.literal("Slow Self"), button -> {
                    PacketDistributor.sendToServer(new C2SSlowMessage(speedMod));
                })
                .bounds(width / 2 + 95, height - 70, 59, 20)
                .tooltip(Tooltip.create(Component.literal("If clicked multiply your walkspeed by the value on the right")))
                .build());
        addRenderableWidget(new DebugSlider(width / 2 + 154, height - 70, 65, 20, Component.literal("Mod: "), Component.literal(""), 0.1, 1, speedMod, 0.05, 2, true) {
            @Override
            protected void applyValue() {
                speedMod = (stepSize * Math.round(Mth.lerp(value, minValue, maxValue) / stepSize));
            }
        });
        builder.withInitialValue(showPaths);
        addRenderableWidget(builder.create(width / 2 - 91, height - 45, 91, 20, Component.literal("Show Paths"), (cycleButton, object) -> {
            showPaths = cycleButton.getValue();
        }));
        addRenderableWidget(Button.builder(Component.literal("Clear Paths"), button -> clearPaths())
                .bounds(width / 2, height - 45, 91, 20)
                .tooltip(Tooltip.create(Component.literal("Mostly unused")))
                .build());
        addRenderableWidget(Button.builder(Component.literal("Spawn Test Structure"), button -> {
                    PacketDistributor.sendToServer(new C2SStructureMessage(true));
                })
                .bounds(width / 2 + 95, height - 45, 125, 20)
                .tooltip(Tooltip.create(Component.literal("Spawns a big structure at 0,79,0 with every mob")))
                .build());
        if (!tabs.isEmpty()) {
            tabs.forEach(tab -> tab.init(width, height));
            Collections.rotate(tabs, -tabShift);
            currentTab = tabs.get(0);
            addRenderableWidget(CycleButton.<DebugTab<?>>builder(debugTab -> Component.literal(debugTab.getClass().getSimpleName()))
                    .withValues(tabs).withInitialValue(currentTab)
                    .create(width / 2, 60, 100, 20, Component.literal("Tab"),
                            (cycleButton, tab) -> {
                                tab.onOpen();
                                addWidget(tab);
                                removeWidget(currentTab);
                                currentTab.onClose();
                                currentTab = tab;
                            }));
            addRenderableWidget(Button.builder(Component.literal("Set default"), button -> {
                        tabShift += tabs.indexOf(currentTab);
                    })
                    .bounds(width / 2, 35, 100, 20)
                    .tooltip(Tooltip.create(Component.literal("Sets the current tab as default tab when opening screen")))
                    .build());
            addWidget(currentTab);
            currentTab.onOpen();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (currentTab != null) {
            currentTab.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        if (entity instanceof Sheep sheep) {
            guiGraphics.drawString(minecraft.font, Component.literal("yRot: " + sheep.getYRot()), 275, 15, 16777215);
            guiGraphics.drawString(minecraft.font, Component.literal("yRotHead: " + sheep.getYHeadRot()), 275, 35, 16777215);
        }
    }

    record PathInfo(BlockPos targetPos, BlockState blockState, boolean below) {
    }
}
