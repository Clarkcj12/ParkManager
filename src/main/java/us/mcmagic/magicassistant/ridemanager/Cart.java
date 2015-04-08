package us.mcmagic.magicassistant.ridemanager;

import net.minecraft.server.v1_8_R2.EntityMinecartRideable;
import net.minecraft.server.v1_8_R2.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.UUID;

/**
 * Created by Marc on 4/1/15
 */
public class Cart extends EntityMinecartRideable {
    private Train train;
    private Vector speed = new Vector(0.0, 0.0, 0.0);
    private UUID passenger;
    private boolean atStation = false;
    private Station station;
    private boolean slowdown = false;
    private boolean playerEnter = true;
    private boolean playerExit;
    private double power = 0;

    public Cart(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
        CraftEntity e;
    }

    @Override
    public void move(double x, double y, double z) {
        Location from = new Location(this.getWorld().getWorld(), locX, locY, locZ);
        Location to = from.clone().add(x, y, z);
        CartMoveEvent e = new CartMoveEvent(this, from, to);
        Bukkit.getPluginManager().callEvent(e);
        if (!e.isCancelled()) {
            if (!slowdown) {
                updateSpeed();
                setSpeed(getSpeed());
            }
            super.move(x, y, z);
            return;
        }
        super.motX = 0.0;
        super.motY = 0.0;
        super.motZ = 0.0;
        super.positionChanged = false;
    }

    @Override
    public void die() {
        if (atStation) {
            return;
        }
        CartDestroyEvent e = new CartDestroyEvent(this);
        if (!e.isCancelled()) {
            getBukkitEntity().getWorld().playSound(getBukkitEntity().getLocation(), Sound.FIZZ, 10, 2);
            super.die();
        }
    }

    public boolean hasPassenger() {
        return passenger != null;
    }

    public void setPassenger(UUID passenger) {
        this.passenger = passenger;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getPower() {
        return power;
    }

    public void updateSpeed() {
        speed = MagicAssistant.getInstance().rideManager.getVector(getDirection(), power);
    }

    public Vector getSpeed() {
        return speed;
    }

    public void setSpeed(Vector speed) {
        this.speed = speed;
        motX = speed.getX();
        motZ = speed.getZ();
        velocityChanged = true;
    }

    public Station getStation() {
        return station;
    }

    public void removeStation() {
        station = null;
        atStation = false;
    }

    public boolean isAtStation() {
        return atStation;
    }

    public void setStation(Station s) {
        atStation = s != null;
        station = s;
        Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                setSpeed(MagicAssistant.getInstance().rideManager.getVector(getDirection(), station.getLaunchPower()));
                removeStation();
            }
        }, (long) (s.getLength() * 20));
    }

    public UUID getPassenger() {
        return passenger;
    }

    private void checkTrainNotNull() {
        if (train == null) {
            train = new Train(this);
        }
    }

    public void addCartToTrain(Cart cart) {
        checkTrainNotNull();
        train.addCart(cart);
    }

    public void removeCartFromTrain(Cart cart) {
        checkTrainNotNull();
        train.removeCart(cart);
    }

    public Train getTrain() {
        return train;
    }

    @Override
    public EnumMinecartType s() {
        return EnumMinecartType.RIDEABLE;
    }

    public void setPlayerEnter(boolean playerEnter) {
        this.playerEnter = playerEnter;
    }

    public boolean playerCanEnter() {
        return playerEnter;
    }

    public void setPlayerExit(boolean playerExit) {
        this.playerExit = playerExit;
    }

    public boolean playerCanExit() {
        return playerExit;
    }

    public boolean canSlowdown() {
        return slowdown;
    }

    public void setSlowdown(boolean slowdown) {
        this.slowdown = slowdown;
    }

    /*//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.minecraft.server.v1_8_R2;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.server.v1_8_R2.AxisAlignedBB;
import net.minecraft.server.v1_8_R2.BlockCobbleWall;
import net.minecraft.server.v1_8_R2.BlockFence;
import net.minecraft.server.v1_8_R2.BlockFenceGate;
import net.minecraft.server.v1_8_R2.BlockFluids;
import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.Blocks;
import net.minecraft.server.v1_8_R2.ChatComponentText;
import net.minecraft.server.v1_8_R2.ChatHoverable;
import net.minecraft.server.v1_8_R2.CommandObjectiveExecutor;
import net.minecraft.server.v1_8_R2.CrashReport;
import net.minecraft.server.v1_8_R2.CrashReportSystemDetails;
import net.minecraft.server.v1_8_R2.DamageSource;
import net.minecraft.server.v1_8_R2.DataWatcher;
import net.minecraft.server.v1_8_R2.EnchantmentManager;
import net.minecraft.server.v1_8_R2.EnchantmentProtection;
import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.EntityInsentient;
import net.minecraft.server.v1_8_R2.EntityItem;
import net.minecraft.server.v1_8_R2.EntityLightning;
import net.minecraft.server.v1_8_R2.EntityLiving;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.EntityTameableAnimal;
import net.minecraft.server.v1_8_R2.EntityTypes;
import net.minecraft.server.v1_8_R2.EnumDirection;
import net.minecraft.server.v1_8_R2.EnumParticle;
import net.minecraft.server.v1_8_R2.Explosion;
import net.minecraft.server.v1_8_R2.IBlockData;
import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.ICommandListener;
import net.minecraft.server.v1_8_R2.Item;
import net.minecraft.server.v1_8_R2.ItemStack;
import net.minecraft.server.v1_8_R2.LocaleI18n;
import net.minecraft.server.v1_8_R2.Material;
import net.minecraft.server.v1_8_R2.MathHelper;
import net.minecraft.server.v1_8_R2.MinecraftServer;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import net.minecraft.server.v1_8_R2.NBTTagDouble;
import net.minecraft.server.v1_8_R2.NBTTagFloat;
import net.minecraft.server.v1_8_R2.NBTTagList;
import net.minecraft.server.v1_8_R2.ReportedException;
import net.minecraft.server.v1_8_R2.Vec3D;
import net.minecraft.server.v1_8_R2.World;
import net.minecraft.server.v1_8_R2.WorldServer;
import net.minecraft.server.v1_8_R2.Block.StepSound;
import net.minecraft.server.v1_8_R2.BlockPosition.MutableBlockPosition;
import net.minecraft.server.v1_8_R2.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_8_R2.CommandObjectiveExecutor.EnumCommandResult;
import net.minecraft.server.v1_8_R2.EnumDirection.EnumAxis;
import net.minecraft.server.v1_8_R2.EnumDirection.EnumAxisDirection;
import net.minecraft.server.v1_8_R2.ShapeDetector.ShapeDetectorCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TravelAgent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R2.CraftServer;
import org.bukkit.craftbukkit.v1_8_R2.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.SpigotTimings;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.projectiles.ProjectileSource;
import org.spigotmc.ActivationRange;
import org.spigotmc.CustomTimingsHandler;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public abstract class Entity implements ICommandListener {
    private static final int CURRENT_LEVEL = 2;
    private static final AxisAlignedBB a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static int entityCount;
    private int id;
    public double j;
    public boolean k;
    public Entity passenger;
    public Entity vehicle;
    public boolean attachedToPlayer;
    public World world;
    public double lastX;
    public double lastY;
    public double lastZ;
    public double locX;
    public double locY;
    public double locZ;
    public double motX;
    public double motY;
    public double motZ;
    public float yaw;
    public float pitch;
    public float lastYaw;
    public float lastPitch;
    private AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean positionChanged;
    public boolean E;
    public boolean F;
    public boolean velocityChanged;
    protected boolean H;
    private boolean g;
    public boolean dead;
    public float width;
    public float length;
    public float L;
    public float M;
    public float N;
    public float fallDistance;
    private int h;
    public double P;
    public double Q;
    public double R;
    public float S;
    public boolean noclip;
    public float U;
    protected Random random;
    public int ticksLived;
    public int maxFireTicks;
    public int fireTicks;
    public boolean inWater;
    public int noDamageTicks;
    protected boolean justCreated;
    protected boolean fireProof;
    protected DataWatcher datawatcher;
    private double ar;
    private double as;
    public boolean ad;
    public int ae;
    public int af;
    public int ag;
    public boolean ah;
    public boolean ai;
    public int portalCooldown;
    protected boolean ak;
    protected int al;
    public int dimension;
    protected BlockPosition an;
    protected Vec3D ao;
    protected EnumDirection ap;
    private boolean invulnerable;
    protected UUID uniqueID;
    private final CommandObjectiveExecutor au;
    public boolean valid;
    public ProjectileSource projectileSource;
    public CustomTimingsHandler tickTimer = SpigotTimings.getEntityTimings(this);
    public final byte activationType = ActivationRange.initializeEntityActivationType(this);
    public final boolean defaultActivationState;
    public long activatedTick = -2147483648L;
    public boolean fromMobSpawner;
    int numCollisions = 0;
    protected CraftEntity bukkitEntity;

    static boolean isLevelAtLeast(NBTTagCompound tag, int level) {
        return tag.hasKey("Bukkit.updateLevel") && tag.getInt("Bukkit.updateLevel") >= level;
    }

    public boolean isAddedToChunk() {
        return this.ad;
    }

    public void inactiveTick() {
    }

    public int getId() {
        return this.id;
    }

    public void d(int i) {
        this.id = i;
    }

    public void G() {
        this.die();
    }

    public Entity(World world) {
        this.id = entityCount++;
        this.j = 1.0D;
        this.boundingBox = a;
        this.width = 0.6F;
        this.length = 1.8F;
        this.h = 1;
        this.random = new Random();
        this.maxFireTicks = 1;
        this.justCreated = true;
        this.uniqueID = MathHelper.a(this.random);
        this.au = new CommandObjectiveExecutor();
        this.world = world;
        this.setPosition(0.0D, 0.0D, 0.0D);
        if(world != null) {
            this.dimension = world.worldProvider.getDimension();
            this.defaultActivationState = ActivationRange.initializeEntityActivationState(this, world.spigotConfig);
        } else {
            this.defaultActivationState = false;
        }

        this.datawatcher = new DataWatcher(this);
        this.datawatcher.a(0, Byte.valueOf((byte)0));
        this.datawatcher.a(1, Short.valueOf((short)300));
        this.datawatcher.a(3, Byte.valueOf((byte)0));
        this.datawatcher.a(2, "");
        this.datawatcher.a(4, Byte.valueOf((byte)0));
        this.h();
    }

    protected abstract void h();

    public DataWatcher getDataWatcher() {
        return this.datawatcher;
    }

    public boolean equals(Object object) {
        return object instanceof Entity?((Entity)object).id == this.id:false;
    }

    public int hashCode() {
        return this.id;
    }

    public void die() {
        this.dead = true;
    }

    public void setSize(float f, float f1) {
        if(f != this.width || f1 != this.length) {
            float f2 = this.width;
            this.width = f;
            this.length = f1;
            this.a(new AxisAlignedBB(this.getBoundingBox().a, this.getBoundingBox().b, this.getBoundingBox().c, this.getBoundingBox().a + (double)this.width, this.getBoundingBox().b + (double)this.length, this.getBoundingBox().c + (double)this.width));
            if(this.width > f2 && !this.justCreated && !this.world.isClientSide) {
                this.move((double)(f2 - this.width), 0.0D, (double)(f2 - this.width));
            }
        }

    }

    protected void setYawPitch(float f, float f1) {
        if(Float.isNaN(f)) {
            f = 0.0F;
        }

        if(f == 1.0F / 0.0 || f == -1.0F / 0.0) {
            if(this instanceof EntityPlayer) {
                this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid yaw");
                ((CraftPlayer)this.getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)");
            }

            f = 0.0F;
        }

        if(Float.isNaN(f1)) {
            f1 = 0.0F;
        }

        if(f1 == 1.0F / 0.0 || f1 == -1.0F / 0.0) {
            if(this instanceof EntityPlayer) {
                this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid pitch");
                ((CraftPlayer)this.getBukkitEntity()).kickPlayer("Infinite pitch (Hacking?)");
            }

            f1 = 0.0F;
        }

        this.yaw = f % 360.0F;
        this.pitch = f1 % 360.0F;
    }

    public void setPosition(double d0, double d1, double d2) {
        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        float f = this.width / 2.0F;
        float f1 = this.length;
        this.a(new AxisAlignedBB(d0 - (double)f, d1, d2 - (double)f, d0 + (double)f, d1 + (double)f1, d2 + (double)f));
    }

    public void t_() {
        this.K();
    }

    public void K() {
        this.world.methodProfiler.a("entityBaseTick");
        if(this.vehicle != null && this.vehicle.dead) {
            this.vehicle = null;
        }

        this.L = this.M;
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
        if(!this.world.isClientSide && this.world instanceof WorldServer) {
            this.world.methodProfiler.a("portal");
            MinecraftServer minecraftserver = ((WorldServer)this.world).getMinecraftServer();
            int i = this.L();
            if(this.ak) {
                if(this.vehicle == null && this.al++ >= i) {
                    this.al = i;
                    this.portalCooldown = this.aq();
                    byte b0;
                    if(this.world.worldProvider.getDimension() == -1) {
                        b0 = 0;
                    } else {
                        b0 = -1;
                    }

                    this.c(b0);
                }

                this.ak = false;
            } else {
                if(this.al > 0) {
                    this.al -= 4;
                }

                if(this.al < 0) {
                    this.al = 0;
                }
            }

            if(this.portalCooldown > 0) {
                --this.portalCooldown;
            }

            this.world.methodProfiler.b();
        }

        this.Y();
        this.W();
        if(this.world.isClientSide) {
            this.fireTicks = 0;
        } else if(this.fireTicks > 0) {
            if(this.fireProof) {
                this.fireTicks -= 4;
                if(this.fireTicks < 0) {
                    this.fireTicks = 0;
                }
            } else {
                if(this.fireTicks % 20 == 0) {
                    this.damageEntity(DamageSource.BURN, 1.0F);
                }

                --this.fireTicks;
            }
        }

        if(this.ab()) {
            this.burnFromLava();
            this.fallDistance *= 0.5F;
        }

        if(this.locY < -64.0D) {
            this.O();
        }

        if(!this.world.isClientSide) {
            this.b(0, this.fireTicks > 0);
        }

        this.justCreated = false;
        this.world.methodProfiler.b();
    }

    public int L() {
        return 0;
    }

    protected void burnFromLava() {
        if(!this.fireProof) {
            this.damageEntity(DamageSource.LAVA, 4.0F);
            if(this instanceof EntityLiving) {
                if(this.fireTicks <= 0) {
                    Object damager = null;
                    CraftEntity damagee = this.getBukkitEntity();
                    EntityCombustByBlockEvent combustEvent = new EntityCombustByBlockEvent((Block)damager, damagee, 15);
                    this.world.getServer().getPluginManager().callEvent(combustEvent);
                    if(!combustEvent.isCancelled()) {
                        this.setOnFire(combustEvent.getDuration());
                    }
                } else {
                    this.setOnFire(15);
                }

                return;
            }

            this.setOnFire(15);
        }

    }

    public void setOnFire(int i) {
        int j = i * 20;
        j = EnchantmentProtection.a(this, j);
        if(this.fireTicks < j) {
            this.fireTicks = j;
        }

    }

    public void extinguish() {
        this.fireTicks = 0;
    }

    protected void O() {
        this.die();
    }

    public boolean c(double d0, double d1, double d2) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().c(d0, d1, d2);
        return this.b(axisalignedbb);
    }

    private boolean b(AxisAlignedBB axisalignedbb) {
        return this.world.getCubes(this, axisalignedbb).isEmpty() && !this.world.containsLiquid(axisalignedbb);
    }

    public void move(double d0, double d1, double d2) {
        SpigotTimings.entityMoveTimer.startTiming();
        if(this.noclip) {
            this.a(this.getBoundingBox().c(d0, d1, d2));
            this.recalcPosition();
        } else {
            try {
                this.checkBlockCollisions();
            } catch (Throwable var84) {
                CrashReport crashreport = CrashReport.a(var84, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            if(d0 == 0.0D && d1 == 0.0D && d2 == 0.0D && this.vehicle == null && this.passenger == null) {
                return;
            }

            this.world.methodProfiler.a("move");
            double d3 = this.locX;
            double d4 = this.locY;
            double d5 = this.locZ;
            if(this.H) {
                this.H = false;
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            double d6 = d0;
            double d7 = d1;
            double d8 = d2;
            boolean flag = this.onGround && this.isSneaking() && this instanceof EntityHuman;
            if(flag) {
                double d9;
                for(d9 = 0.05D; d0 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, 0.0D)).isEmpty(); d6 = d0) {
                    if(d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if(d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }
                }

                for(; d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(0.0D, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if(d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if(d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }

                for(; d0 != 0.0D && d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if(d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if(d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }

                    d6 = d0;
                    if(d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if(d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }
            }

            List list = this.world.getCubes(this, this.getBoundingBox().a(d0, d1, d2));
            AxisAlignedBB axisalignedbb = this.getBoundingBox();

            AxisAlignedBB axisalignedbb1;
            for(Iterator flag1 = list.iterator(); flag1.hasNext(); d1 = axisalignedbb1.b(this.getBoundingBox(), d1)) {
                axisalignedbb1 = (AxisAlignedBB)flag1.next();
            }

            this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
            boolean var85 = this.onGround || d7 != d1 && d7 < 0.0D;

            Iterator iterator1;
            AxisAlignedBB axisalignedbb2;
            for(iterator1 = list.iterator(); iterator1.hasNext(); d0 = axisalignedbb2.a(this.getBoundingBox(), d0)) {
                axisalignedbb2 = (AxisAlignedBB)iterator1.next();
            }

            this.a(this.getBoundingBox().c(d0, 0.0D, 0.0D));

            for(iterator1 = list.iterator(); iterator1.hasNext(); d2 = axisalignedbb2.c(this.getBoundingBox(), d2)) {
                axisalignedbb2 = (AxisAlignedBB)iterator1.next();
            }

            this.a(this.getBoundingBox().c(0.0D, 0.0D, d2));
            if(this.S > 0.0F && var85 && (d6 != d0 || d8 != d2)) {
                double d10 = d0;
                double d11 = d1;
                double d12 = d2;
                AxisAlignedBB event = this.getBoundingBox();
                this.a(axisalignedbb);
                d1 = (double)this.S;
                List event1 = this.world.getCubes(this, this.getBoundingBox().a(d6, d1, d8));
                AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
                AxisAlignedBB axisalignedbb5 = axisalignedbb4.a(d6, 0.0D, d8);
                double d13 = d1;

                AxisAlignedBB axisalignedbb6;
                for(Iterator iterator2 = event1.iterator(); iterator2.hasNext(); d13 = axisalignedbb6.b(axisalignedbb5, d13)) {
                    axisalignedbb6 = (AxisAlignedBB)iterator2.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, d13, 0.0D);
                double d14 = d6;

                AxisAlignedBB axisalignedbb7;
                for(Iterator iterator3 = event1.iterator(); iterator3.hasNext(); d14 = axisalignedbb7.a(axisalignedbb4, d14)) {
                    axisalignedbb7 = (AxisAlignedBB)iterator3.next();
                }

                axisalignedbb4 = axisalignedbb4.c(d14, 0.0D, 0.0D);
                double d15 = d8;

                AxisAlignedBB axisalignedbb8;
                for(Iterator axisalignedbb9 = event1.iterator(); axisalignedbb9.hasNext(); d15 = axisalignedbb8.c(axisalignedbb4, d15)) {
                    axisalignedbb8 = (AxisAlignedBB)axisalignedbb9.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, 0.0D, d15);
                AxisAlignedBB var89 = this.getBoundingBox();
                double d16 = d1;

                AxisAlignedBB axisalignedbb10;
                for(Iterator iterator5 = event1.iterator(); iterator5.hasNext(); d16 = axisalignedbb10.b(var89, d16)) {
                    axisalignedbb10 = (AxisAlignedBB)iterator5.next();
                }

                var89 = var89.c(0.0D, d16, 0.0D);
                double d17 = d6;

                AxisAlignedBB axisalignedbb11;
                for(Iterator iterator6 = event1.iterator(); iterator6.hasNext(); d17 = axisalignedbb11.a(var89, d17)) {
                    axisalignedbb11 = (AxisAlignedBB)iterator6.next();
                }

                var89 = var89.c(d17, 0.0D, 0.0D);
                double d18 = d8;

                AxisAlignedBB axisalignedbb12;
                for(Iterator iterator7 = event1.iterator(); iterator7.hasNext(); d18 = axisalignedbb12.c(var89, d18)) {
                    axisalignedbb12 = (AxisAlignedBB)iterator7.next();
                }

                var89 = var89.c(0.0D, 0.0D, d18);
                double d19 = d14 * d14 + d15 * d15;
                double d20 = d17 * d17 + d18 * d18;
                if(d19 > d20) {
                    d0 = d14;
                    d2 = d15;
                    d1 = -d13;
                    this.a(axisalignedbb4);
                } else {
                    d0 = d17;
                    d2 = d18;
                    d1 = -d16;
                    this.a(var89);
                }

                AxisAlignedBB axisalignedbb13;
                for(Iterator iterator8 = event1.iterator(); iterator8.hasNext(); d1 = axisalignedbb13.b(this.getBoundingBox(), d1)) {
                    axisalignedbb13 = (AxisAlignedBB)iterator8.next();
                }

                this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
                if(d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
                    d0 = d10;
                    d1 = d11;
                    d2 = d12;
                    this.a(event);
                }
            }

            this.world.methodProfiler.b();
            this.world.methodProfiler.a("rest");
            this.recalcPosition();
            this.positionChanged = d6 != d0 || d8 != d2;
            this.E = d7 != d1;
            this.onGround = this.E && d7 < 0.0D;
            this.F = this.positionChanged || this.E;
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            net.minecraft.server.v1_8_R2.Block block = this.world.getType(blockposition).getBlock();
            if(block.getMaterial() == Material.AIR) {
                net.minecraft.server.v1_8_R2.Block flag2 = this.world.getType(blockposition.down()).getBlock();
                if(flag2 instanceof BlockFence || flag2 instanceof BlockCobbleWall || flag2 instanceof BlockFenceGate) {
                    block = flag2;
                    blockposition = blockposition.down();
                }
            }

            this.a(d1, this.onGround, block, blockposition);
            if(d6 != d0) {
                this.motX = 0.0D;
            }

            if(d8 != d2) {
                this.motZ = 0.0D;
            }

            if(d7 != d1) {
                block.a(this.world, this);
            }

            if(this.positionChanged && this.getBukkitEntity() instanceof Vehicle) {
                Vehicle var91 = (Vehicle)this.getBukkitEntity();
                Block var88 = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
                if(d6 > d0) {
                    var88 = var88.getRelative(BlockFace.EAST);
                } else if(d6 < d0) {
                    var88 = var88.getRelative(BlockFace.WEST);
                } else if(d8 > d2) {
                    var88 = var88.getRelative(BlockFace.SOUTH);
                } else if(d8 < d2) {
                    var88 = var88.getRelative(BlockFace.NORTH);
                }

                VehicleBlockCollisionEvent var87 = new VehicleBlockCollisionEvent(var91, var88);
                this.world.getServer().getPluginManager().callEvent(var87);
            }

            if(this.s_() && !flag && this.vehicle == null) {
                double d21 = this.locX - d3;
                double d22 = this.locY - d4;
                double d23 = this.locZ - d5;
                if(block != Blocks.LADDER) {
                    d22 = 0.0D;
                }

                if(block != null) {
                    ;
                }

                this.M = (float)((double)this.M + (double)MathHelper.sqrt(d21 * d21 + d23 * d23) * 0.6D);
                this.N = (float)((double)this.N + (double)MathHelper.sqrt(d21 * d21 + d22 * d22 + d23 * d23) * 0.6D);
                if(this.N > (float)this.h && block.getMaterial() != Material.AIR) {
                    this.h = (int)this.N + 1;
                    if(this.V()) {
                        float f = MathHelper.sqrt(this.motX * this.motX * 0.20000000298023224D + this.motY * this.motY + this.motZ * this.motZ * 0.20000000298023224D) * 0.35F;
                        if(f > 1.0F) {
                            f = 1.0F;
                        }

                        this.makeSound(this.P(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    }

                    this.a(blockposition, block);
                    block.a(this.world, blockposition, this);
                }
            }

            boolean var90 = this.U();
            if(this.world.e(this.getBoundingBox().shrink(0.0010D, 0.0010D, 0.0010D))) {
                this.burn(1.0F);
                if(!var90) {
                    ++this.fireTicks;
                    if(this.fireTicks <= 0) {
                        EntityCombustEvent var86 = new EntityCombustEvent(this.getBukkitEntity(), 8);
                        this.world.getServer().getPluginManager().callEvent(var86);
                        if(!var86.isCancelled()) {
                            this.setOnFire(var86.getDuration());
                        }
                    } else {
                        this.setOnFire(8);
                    }
                }
            } else if(this.fireTicks <= 0) {
                this.fireTicks = -this.maxFireTicks;
            }

            if(var90 && this.fireTicks > 0) {
                this.makeSound("random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.fireTicks = -this.maxFireTicks;
            }

            this.world.methodProfiler.b();
        }

        SpigotTimings.entityMoveTimer.stopTiming();
    }

    private void recalcPosition() {
        this.locX = (this.getBoundingBox().a + this.getBoundingBox().d) / 2.0D;
        this.locY = this.getBoundingBox().b;
        this.locZ = (this.getBoundingBox().c + this.getBoundingBox().f) / 2.0D;
    }

    protected String P() {
        return "game.neutral.swim";
    }

    protected void checkBlockCollisions() {
        BlockPosition blockposition = new BlockPosition(this.getBoundingBox().a + 0.0010D, this.getBoundingBox().b + 0.0010D, this.getBoundingBox().c + 0.0010D);
        BlockPosition blockposition1 = new BlockPosition(this.getBoundingBox().d - 0.0010D, this.getBoundingBox().e - 0.0010D, this.getBoundingBox().f - 0.0010D);
        if(this.world.areChunksLoadedBetween(blockposition, blockposition1)) {
            for(int i = blockposition.getX(); i <= blockposition1.getX(); ++i) {
                for(int j = blockposition.getY(); j <= blockposition1.getY(); ++j) {
                    for(int k = blockposition.getZ(); k <= blockposition1.getZ(); ++k) {
                        BlockPosition blockposition2 = new BlockPosition(i, j, k);
                        IBlockData iblockdata = this.world.getType(blockposition2);

                        try {
                            iblockdata.getBlock().a(this.world, blockposition2, iblockdata, this);
                        } catch (Throwable var11) {
                            CrashReport crashreport = CrashReport.a(var11, "Colliding entity with block");
                            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being collided with");
                            CrashReportSystemDetails.a(crashreportsystemdetails, blockposition2, iblockdata);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }

    }

    protected void a(BlockPosition blockposition, net.minecraft.server.v1_8_R2.Block block) {
        StepSound block_stepsound = block.stepSound;
        if(this.world.getType(blockposition.up()).getBlock() == Blocks.SNOW_LAYER) {
            block_stepsound = Blocks.SNOW_LAYER.stepSound;
            this.makeSound(block_stepsound.getStepSound(), block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
        } else if(!block.getMaterial().isLiquid()) {
            this.makeSound(block_stepsound.getStepSound(), block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
        }

    }

    public void makeSound(String s, float f, float f1) {
        if(!this.R()) {
            this.world.makeSound(this, s, f, f1);
        }

    }

    public boolean R() {
        return this.datawatcher.getByte(4) == 1;
    }

    public void b(boolean flag) {
        this.datawatcher.watch(4, Byte.valueOf((byte)(flag?1:0)));
    }

    protected boolean s_() {
        return true;
    }

    protected void a(double d0, boolean flag, net.minecraft.server.v1_8_R2.Block block, BlockPosition blockposition) {
        if(flag) {
            if(this.fallDistance > 0.0F) {
                if(block != null) {
                    block.a(this.world, blockposition, this, this.fallDistance);
                } else {
                    this.e(this.fallDistance, 1.0F);
                }

                this.fallDistance = 0.0F;
            }
        } else if(d0 < 0.0D) {
            this.fallDistance = (float)((double)this.fallDistance - d0);
        }

    }

    public AxisAlignedBB S() {
        return null;
    }

    protected void burn(float i) {
        if(!this.fireProof) {
            this.damageEntity(DamageSource.FIRE, i);
        }

    }

    public final boolean isFireProof() {
        return this.fireProof;
    }

    public void e(float f, float f1) {
        if(this.passenger != null) {
            this.passenger.e(f, f1);
        }

    }

    public boolean U() {
        return this.inWater || this.world.isRainingAt(new BlockPosition(this.locX, this.locY, this.locZ)) || this.world.isRainingAt(new BlockPosition(this.locX, this.locY + (double)this.length, this.locZ));
    }

    public boolean V() {
        return this.inWater;
    }

    public boolean W() {
        if(this.world.a(this.getBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.0010D, 0.0010D, 0.0010D), Material.WATER, this)) {
            if(!this.inWater && !this.justCreated) {
                this.X();
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fireTicks = 0;
        } else {
            this.inWater = false;
        }

        return this.inWater;
    }

    protected void X() {
        float f = MathHelper.sqrt(this.motX * this.motX * 0.20000000298023224D + this.motY * this.motY + this.motZ * this.motZ * 0.20000000298023224D) * 0.2F;
        if(f > 1.0F) {
            f = 1.0F;
        }

        this.makeSound(this.aa(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        float f1 = (float)MathHelper.floor(this.getBoundingBox().b);

        int i;
        float f2;
        float f3;
        for(i = 0; (float)i < 1.0F + this.width * 20.0F; ++i) {
            f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX + (double)f2, (double)(f1 + 1.0F), this.locZ + (double)f3, this.motX, this.motY - (double)(this.random.nextFloat() * 0.2F), this.motZ, new int[0]);
        }

        for(i = 0; (float)i < 1.0F + this.width * 20.0F; ++i) {
            f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            this.world.addParticle(EnumParticle.WATER_SPLASH, this.locX + (double)f2, (double)(f1 + 1.0F), this.locZ + (double)f3, this.motX, this.motY, this.motZ, new int[0]);
        }

    }

    public void Y() {
        if(this.isSprinting() && !this.V()) {
            this.Z();
        }

    }

    protected void Z() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locY - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        IBlockData iblockdata = this.world.getType(blockposition);
        net.minecraft.server.v1_8_R2.Block block = iblockdata.getBlock();
        if(block.b() != -1) {
            this.world.addParticle(EnumParticle.BLOCK_CRACK, this.locX + ((double)this.random.nextFloat() - 0.5D) * (double)this.width, this.getBoundingBox().b + 0.1D, this.locZ + ((double)this.random.nextFloat() - 0.5D) * (double)this.width, -this.motX * 4.0D, 1.5D, -this.motZ * 4.0D, new int[]{net.minecraft.server.v1_8_R2.Block.getCombinedId(iblockdata)});
        }

    }

    protected String aa() {
        return "game.neutral.swim.splash";
    }

    public boolean a(Material material) {
        double d0 = this.locY + (double)this.getHeadHeight();
        BlockPosition blockposition = new BlockPosition(this.locX, d0, this.locZ);
        IBlockData iblockdata = this.world.getType(blockposition);
        net.minecraft.server.v1_8_R2.Block block = iblockdata.getBlock();
        if(block.getMaterial() == material) {
            float f = BlockFluids.b(iblockdata.getBlock().toLegacyData(iblockdata)) - 0.11111111F;
            float f1 = (float)(blockposition.getY() + 1) - f;
            boolean flag = d0 < (double)f1;
            return !flag && this instanceof EntityHuman?false:flag;
        } else {
            return false;
        }
    }

    public boolean ab() {
        return this.world.a(this.getBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.LAVA);
    }

    public void a(float f, float f1, float f2) {
        float f3 = f * f + f1 * f1;
        if(f3 >= 1.0E-4F) {
            f3 = MathHelper.c(f3);
            if(f3 < 1.0F) {
                f3 = 1.0F;
            }

            f3 = f2 / f3;
            f *= f3;
            f1 *= f3;
            float f4 = MathHelper.sin(this.yaw * 3.1415927F / 180.0F);
            float f5 = MathHelper.cos(this.yaw * 3.1415927F / 180.0F);
            this.motX += (double)(f * f5 - f1 * f4);
            this.motZ += (double)(f1 * f5 + f * f4);
        }

    }

    public float c(float f) {
        BlockPosition blockposition = new BlockPosition(this.locX, this.locY + (double)this.getHeadHeight(), this.locZ);
        return this.world.isLoaded(blockposition)?this.world.o(blockposition):0.0F;
    }

    public void spawnIn(World world) {
        if(world == null) {
            this.die();
            this.world = ((CraftWorld)Bukkit.getServer().getWorlds().get(0)).getHandle();
        } else {
            this.world = world;
        }
    }

    public void setLocation(double d0, double d1, double d2, float f, float f1) {
        this.lastX = this.locX = d0;
        this.lastY = this.locY = d1;
        this.lastZ = this.locZ = d2;
        this.lastYaw = this.yaw = f;
        this.lastPitch = this.pitch = f1;
        double d3 = (double)(this.lastYaw - f);
        if(d3 < -180.0D) {
            this.lastYaw += 360.0F;
        }

        if(d3 >= 180.0D) {
            this.lastYaw -= 360.0F;
        }

        this.setPosition(this.locX, this.locY, this.locZ);
        this.setYawPitch(f, f1);
    }

    public void setPositionRotation(BlockPosition blockposition, float f, float f1) {
        this.setPositionRotation((double)blockposition.getX() + 0.5D, (double)blockposition.getY(), (double)blockposition.getZ() + 0.5D, f, f1);
    }

    public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
        this.P = this.lastX = this.locX = d0;
        this.Q = this.lastY = this.locY = d1;
        this.R = this.lastZ = this.locZ = d2;
        this.yaw = f;
        this.pitch = f1;
        this.setPosition(this.locX, this.locY, this.locZ);
    }

    public float g(Entity entity) {
        float f = (float)(this.locX - entity.locX);
        float f1 = (float)(this.locY - entity.locY);
        float f2 = (float)(this.locZ - entity.locZ);
        return MathHelper.c(f * f + f1 * f1 + f2 * f2);
    }

    public double e(double d0, double d1, double d2) {
        double d3 = this.locX - d0;
        double d4 = this.locY - d1;
        double d5 = this.locZ - d2;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double b(BlockPosition blockposition) {
        return blockposition.c(this.locX, this.locY, this.locZ);
    }

    public double c(BlockPosition blockposition) {
        return blockposition.d(this.locX, this.locY, this.locZ);
    }

    public double f(double d0, double d1, double d2) {
        double d3 = this.locX - d0;
        double d4 = this.locY - d1;
        double d5 = this.locZ - d2;
        return (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
    }

    public double h(Entity entity) {
        double d0 = this.locX - entity.locX;
        double d1 = this.locY - entity.locY;
        double d2 = this.locZ - entity.locZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void d(EntityHuman entityhuman) {
    }

    public void collide(Entity entity) {
        if(entity.passenger != this && entity.vehicle != this && !entity.noclip && !this.noclip) {
            double d0 = entity.locX - this.locX;
            double d1 = entity.locZ - this.locZ;
            double d2 = MathHelper.a(d0, d1);
            if(d2 >= 0.009999999776482582D) {
                d2 = (double)MathHelper.sqrt(d2);
                d0 /= d2;
                d1 /= d2;
                double d3 = 1.0D / d2;
                if(d3 > 1.0D) {
                    d3 = 1.0D;
                }

                d0 *= d3;
                d1 *= d3;
                d0 *= 0.05000000074505806D;
                d1 *= 0.05000000074505806D;
                d0 *= (double)(1.0F - this.U);
                d1 *= (double)(1.0F - this.U);
                if(this.passenger == null) {
                    this.g(-d0, 0.0D, -d1);
                }

                if(entity.passenger == null) {
                    entity.g(d0, 0.0D, d1);
                }
            }
        }

    }

    public void g(double d0, double d1, double d2) {
        this.motX += d0;
        this.motY += d1;
        this.motZ += d2;
        this.ai = true;
    }

    protected void ac() {
        this.velocityChanged = true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if(this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.ac();
            return false;
        }
    }

    public Vec3D d(float f) {
        if(f == 1.0F) {
            return this.f(this.pitch, this.yaw);
        } else {
            float f1 = this.lastPitch + (this.pitch - this.lastPitch) * f;
            float f2 = this.lastYaw + (this.yaw - this.lastYaw) * f;
            return this.f(f1, f2);
        }
    }

    protected final Vec3D f(float f, float f1) {
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        return new Vec3D((double)(f3 * f4), (double)f5, (double)(f2 * f4));
    }

    public boolean ad() {
        return false;
    }

    public boolean ae() {
        return false;
    }

    public void b(Entity entity, int i) {
    }

    public boolean c(NBTTagCompound nbttagcompound) {
        String s = this.ag();
        if(!this.dead && s != null) {
            nbttagcompound.setString("id", s);
            this.e(nbttagcompound);
            return true;
        } else {
            return false;
        }
    }

    public boolean d(NBTTagCompound nbttagcompound) {
        String s = this.ag();
        if(!this.dead && s != null && this.passenger == null) {
            nbttagcompound.setString("id", s);
            this.e(nbttagcompound);
            return true;
        } else {
            return false;
        }
    }

    public void e(NBTTagCompound nbttagcompound) {
        try {
            nbttagcompound.set("Pos", this.a(new double[]{this.locX, this.locY, this.locZ}));
            nbttagcompound.set("Motion", this.a(new double[]{this.motX, this.motY, this.motZ}));
            if(Float.isNaN(this.yaw)) {
                this.yaw = 0.0F;
            }

            if(Float.isNaN(this.pitch)) {
                this.pitch = 0.0F;
            }

            nbttagcompound.set("Rotation", this.a(new float[]{this.yaw, this.pitch}));
            nbttagcompound.setFloat("FallDistance", this.fallDistance);
            nbttagcompound.setShort("Fire", (short)this.fireTicks);
            nbttagcompound.setShort("Air", (short)this.getAirTicks());
            nbttagcompound.setBoolean("OnGround", this.onGround);
            nbttagcompound.setInt("Dimension", this.dimension);
            nbttagcompound.setBoolean("Invulnerable", this.invulnerable);
            nbttagcompound.setInt("PortalCooldown", this.portalCooldown);
            nbttagcompound.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
            nbttagcompound.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
            nbttagcompound.setLong("WorldUUIDLeast", this.world.getDataManager().getUUID().getLeastSignificantBits());
            nbttagcompound.setLong("WorldUUIDMost", this.world.getDataManager().getUUID().getMostSignificantBits());
            nbttagcompound.setInt("Bukkit.updateLevel", 2);
            nbttagcompound.setInt("Spigot.ticksLived", this.ticksLived);
            if(this.getCustomName() != null && this.getCustomName().length() > 0) {
                nbttagcompound.setString("CustomName", this.getCustomName());
                nbttagcompound.setBoolean("CustomNameVisible", this.getCustomNameVisible());
            }

            this.au.b(nbttagcompound);
            if(this.R()) {
                nbttagcompound.setBoolean("Silent", this.R());
            }

            this.b(nbttagcompound);
            if(this.vehicle != null) {
                NBTTagCompound throwable = new NBTTagCompound();
                if(this.vehicle.c(throwable)) {
                    nbttagcompound.set("Riding", throwable);
                }
            }

        } catch (Throwable var5) {
            CrashReport crashreport = CrashReport.a(var5, "Saving entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being saved");
            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    public void f(NBTTagCompound nbttagcompound) {
        try {
            NBTTagList throwable = nbttagcompound.getList("Pos", 6);
            NBTTagList crashreport1 = nbttagcompound.getList("Motion", 6);
            NBTTagList crashreportsystemdetails1 = nbttagcompound.getList("Rotation", 5);
            this.motX = crashreport1.d(0);
            this.motY = crashreport1.d(1);
            this.motZ = crashreport1.d(2);
            this.lastX = this.P = this.locX = throwable.d(0);
            this.lastY = this.Q = this.locY = throwable.d(1);
            this.lastZ = this.R = this.locZ = throwable.d(2);
            this.lastYaw = this.yaw = crashreportsystemdetails1.e(0);
            this.lastPitch = this.pitch = crashreportsystemdetails1.e(1);
            this.f(this.yaw);
            this.g(this.yaw);
            this.fallDistance = nbttagcompound.getFloat("FallDistance");
            this.fireTicks = nbttagcompound.getShort("Fire");
            this.setAirTicks(nbttagcompound.getShort("Air"));
            this.onGround = nbttagcompound.getBoolean("OnGround");
            this.dimension = nbttagcompound.getInt("Dimension");
            this.invulnerable = nbttagcompound.getBoolean("Invulnerable");
            this.portalCooldown = nbttagcompound.getInt("PortalCooldown");
            if(nbttagcompound.hasKeyOfType("UUIDMost", 4) && nbttagcompound.hasKeyOfType("UUIDLeast", 4)) {
                this.uniqueID = new UUID(nbttagcompound.getLong("UUIDMost"), nbttagcompound.getLong("UUIDLeast"));
            } else if(nbttagcompound.hasKeyOfType("UUID", 8)) {
                this.uniqueID = UUID.fromString(nbttagcompound.getString("UUID"));
            }

            this.setPosition(this.locX, this.locY, this.locZ);
            this.setYawPitch(this.yaw, this.pitch);
            if(nbttagcompound.hasKeyOfType("CustomName", 8) && nbttagcompound.getString("CustomName").length() > 0) {
                this.setCustomName(nbttagcompound.getString("CustomName"));
            }

            this.setCustomNameVisible(nbttagcompound.getBoolean("CustomNameVisible"));
            this.au.a(nbttagcompound);
            this.b(nbttagcompound.getBoolean("Silent"));
            this.a(nbttagcompound);
            if(this.af()) {
                this.setPosition(this.locX, this.locY, this.locZ);
            }

            EntityInsentient bworld;
            if(this instanceof EntityLiving) {
                EntityLiving server = (EntityLiving)this;
                this.ticksLived = nbttagcompound.getInt("Spigot.ticksLived");
                if(server instanceof EntityTameableAnimal && !isLevelAtLeast(nbttagcompound, 2) && !nbttagcompound.getBoolean("PersistenceRequired")) {
                    bworld = (EntityInsentient)server;
                    bworld.persistent = !bworld.isTypeNotPersistent();
                }
            }

            if(!(this.getBukkitEntity() instanceof Vehicle)) {
                if(Math.abs(this.motX) > 10.0D) {
                    this.motX = 0.0D;
                }

                if(Math.abs(this.motY) > 10.0D) {
                    this.motY = 0.0D;
                }

                if(Math.abs(this.motZ) > 10.0D) {
                    this.motZ = 0.0D;
                }
            }

            if(this instanceof EntityPlayer) {
                Server server1 = Bukkit.getServer();
                bworld = null;
                String worldName = nbttagcompound.getString("world");
                Object bworld1;
                if(nbttagcompound.hasKey("WorldUUIDMost") && nbttagcompound.hasKey("WorldUUIDLeast")) {
                    UUID entityPlayer = new UUID(nbttagcompound.getLong("WorldUUIDMost"), nbttagcompound.getLong("WorldUUIDLeast"));
                    bworld1 = server1.getWorld(entityPlayer);
                } else {
                    bworld1 = server1.getWorld(worldName);
                }

                if(bworld1 == null) {
                    EntityPlayer entityPlayer1 = (EntityPlayer)this;
                    bworld1 = ((CraftServer)server1).getServer().getWorldServer(entityPlayer1.dimension).getWorld();
                }

                this.spawnIn(bworld1 == null?null:((CraftWorld)bworld1).getHandle());
            }

        } catch (Throwable var9) {
            CrashReport crashreport = CrashReport.a(var9, "Loading entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being loaded");
            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean af() {
        return true;
    }

    protected final String ag() {
        return EntityTypes.b(this);
    }

    protected abstract void a(NBTTagCompound var1);

    protected abstract void b(NBTTagCompound var1);

    public void ah() {
    }

    protected NBTTagList a(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for(int j = 0; j < i; ++j) {
            double d0 = adouble1[j];
            nbttaglist.add(new NBTTagDouble(d0));
        }

        return nbttaglist;
    }

    protected NBTTagList a(float... afloat) {
        NBTTagList nbttaglist = new NBTTagList();
        float[] afloat1 = afloat;
        int i = afloat.length;

        for(int j = 0; j < i; ++j) {
            float f = afloat1[j];
            nbttaglist.add(new NBTTagFloat(f));
        }

        return nbttaglist;
    }

    public EntityItem a(Item item, int i) {
        return this.a(item, i, 0.0F);
    }

    public EntityItem a(Item item, int i, float f) {
        return this.a(new ItemStack(item, i, 0), f);
    }

    public EntityItem a(ItemStack itemstack, float f) {
        if(itemstack.count != 0 && itemstack.getItem() != null) {
            if(this instanceof EntityLiving && ((EntityLiving)this).drops != null) {
                ((EntityLiving)this).drops.add(CraftItemStack.asBukkitCopy(itemstack));
                return null;
            } else {
                EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY + (double)f, this.locZ, itemstack);
                entityitem.p();
                this.world.addEntity(entityitem);
                return entityitem;
            }
        } else {
            return null;
        }
    }

    public boolean isAlive() {
        return !this.dead;
    }

    public boolean inBlock() {
        if(this.noclip) {
            return false;
        } else {
            MutableBlockPosition blockposition_mutableblockposition = new MutableBlockPosition(-2147483648, -2147483648, -2147483648);

            for(int i = 0; i < 8; ++i) {
                int j = MathHelper.floor(this.locY + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getHeadHeight());
                int k = MathHelper.floor(this.locX + (double)(((float)((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
                int l = MathHelper.floor(this.locZ + (double)(((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F));
                if(blockposition_mutableblockposition.getX() != k || blockposition_mutableblockposition.getY() != j || blockposition_mutableblockposition.getZ() != l) {
                    blockposition_mutableblockposition.c(k, j, l);
                    if(this.world.getType(blockposition_mutableblockposition).getBlock().w()) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean e(EntityHuman entityhuman) {
        return false;
    }

    public AxisAlignedBB j(Entity entity) {
        return null;
    }

    public void ak() {
        if(this.vehicle.dead) {
            this.vehicle = null;
        } else {
            this.motX = 0.0D;
            this.motY = 0.0D;
            this.motZ = 0.0D;
            this.t_();
            if(this.vehicle != null) {
                this.vehicle.al();
                this.as += (double)(this.vehicle.yaw - this.vehicle.lastYaw);

                for(this.ar += (double)(this.vehicle.pitch - this.vehicle.lastPitch); this.as >= 180.0D; this.as -= 360.0D) {
                    ;
                }

                while(this.as < -180.0D) {
                    this.as += 360.0D;
                }

                while(this.ar >= 180.0D) {
                    this.ar -= 360.0D;
                }

                while(this.ar < -180.0D) {
                    this.ar += 360.0D;
                }

                double d0 = this.as * 0.5D;
                double d1 = this.ar * 0.5D;
                float f = 10.0F;
                if(d0 > (double)f) {
                    d0 = (double)f;
                }

                if(d0 < (double)(-f)) {
                    d0 = (double)(-f);
                }

                if(d1 > (double)f) {
                    d1 = (double)f;
                }

                if(d1 < (double)(-f)) {
                    d1 = (double)(-f);
                }

                this.as -= d0;
                this.ar -= d1;
            }
        }

    }

    public void al() {
        if(this.passenger != null) {
            this.passenger.setPosition(this.locX, this.locY + this.an() + this.passenger.am(), this.locZ);
        }

    }

    public double am() {
        return 0.0D;
    }

    public double an() {
        return (double)this.length * 0.75D;
    }

    public CraftEntity getBukkitEntity() {
        if(this.bukkitEntity == null) {
            this.bukkitEntity = CraftEntity.getEntity(this.world.getServer(), this);
        }

        return this.bukkitEntity;
    }

    public void mount(Entity entity) {
        Entity originalVehicle = this.vehicle;
        Entity originalPassenger = this.vehicle == null?null:this.vehicle.passenger;
        PluginManager pluginManager = Bukkit.getPluginManager();
        this.getBukkitEntity();
        this.ar = 0.0D;
        this.as = 0.0D;
        VehicleExitEvent entity1;
        if(entity == null) {
            if(this.vehicle != null) {
                if(this.bukkitEntity instanceof LivingEntity && this.vehicle.getBukkitEntity() instanceof Vehicle) {
                    entity1 = new VehicleExitEvent((Vehicle)this.vehicle.getBukkitEntity(), (LivingEntity)this.bukkitEntity);
                    pluginManager.callEvent(entity1);
                    if(entity1.isCancelled() || this.vehicle != originalVehicle) {
                        return;
                    }
                }

                pluginManager.callEvent(new EntityDismountEvent(this.getBukkitEntity(), this.vehicle.getBukkitEntity()));
                this.setPositionRotation(this.vehicle.locX, this.vehicle.getBoundingBox().b + (double)this.vehicle.length, this.vehicle.locZ, this.yaw, this.pitch);
                this.vehicle.passenger = null;
            }

            this.vehicle = null;
        } else {
            if(this.bukkitEntity instanceof LivingEntity && entity.getBukkitEntity() instanceof Vehicle && entity.world.isChunkLoaded((int)entity.locX >> 4, (int)entity.locZ >> 4, true)) {
                entity1 = null;
                if(this.vehicle != null && this.vehicle.getBukkitEntity() instanceof Vehicle) {
                    entity1 = new VehicleExitEvent((Vehicle)this.vehicle.getBukkitEntity(), (LivingEntity)this.bukkitEntity);
                    pluginManager.callEvent(entity1);
                    if(entity1.isCancelled() || this.vehicle != originalVehicle || this.vehicle != null && this.vehicle.passenger != originalPassenger) {
                        return;
                    }
                }

                VehicleEnterEvent event = new VehicleEnterEvent((Vehicle)entity.getBukkitEntity(), this.bukkitEntity);
                pluginManager.callEvent(event);
                if(event.isCancelled() || this.vehicle != originalVehicle || this.vehicle != null && this.vehicle.passenger != originalPassenger) {
                    if(entity1 != null && this.vehicle == originalVehicle && this.vehicle != null && this.vehicle.passenger == originalPassenger) {
                        this.setPositionRotation(this.vehicle.locX, this.vehicle.getBoundingBox().b + (double)this.vehicle.length, this.vehicle.locZ, this.yaw, this.pitch);
                        this.vehicle.passenger = null;
                        this.vehicle = null;
                    }

                    return;
                }
            }

            if(entity.world.isChunkLoaded((int)entity.locX >> 4, (int)entity.locZ >> 4, true)) {
                EntityMountEvent entity11 = new EntityMountEvent(this.getBukkitEntity(), entity.getBukkitEntity());
                pluginManager.callEvent(entity11);
                if(entity11.isCancelled()) {
                    return;
                }
            }

            if(this.vehicle != null) {
                this.vehicle.passenger = null;
            }

            if(entity != null) {
                for(Entity entity12 = entity.vehicle; entity12 != null; entity12 = entity12.vehicle) {
                    if(entity12 == this) {
                        return;
                    }
                }
            }

            this.vehicle = entity;
            entity.passenger = this;
        }

    }

    public float ao() {
        return 0.1F;
    }

    public Vec3D ap() {
        return null;
    }

    public void d(BlockPosition blockposition) {
        if(this.portalCooldown > 0) {
            this.portalCooldown = this.aq();
        } else {
            if(!this.world.isClientSide && !blockposition.equals(this.an)) {
                this.an = blockposition;
                ShapeDetectorCollection shapedetector_shapedetectorcollection = Blocks.PORTAL.f(this.world, blockposition);
                double d0 = shapedetector_shapedetectorcollection.b().k() == EnumAxis.X?(double)shapedetector_shapedetectorcollection.a().getZ():(double)shapedetector_shapedetectorcollection.a().getX();
                double d1 = shapedetector_shapedetectorcollection.b().k() == EnumAxis.X?this.locZ:this.locX;
                d1 = Math.abs(MathHelper.c(d1 - (double)(shapedetector_shapedetectorcollection.b().e().c() == EnumAxisDirection.NEGATIVE?1:0), d0, d0 - (double)shapedetector_shapedetectorcollection.d()));
                double d2 = MathHelper.c(this.locY - 1.0D, (double)shapedetector_shapedetectorcollection.a().getY(), (double)(shapedetector_shapedetectorcollection.a().getY() - shapedetector_shapedetectorcollection.e()));
                this.ao = new Vec3D(d1, d2, 0.0D);
                this.ap = shapedetector_shapedetectorcollection.b();
            }

            this.ak = true;
        }

    }

    public int aq() {
        return 300;
    }

    public ItemStack[] getEquipment() {
        return null;
    }

    public void setEquipment(int i, ItemStack itemstack) {
    }

    public boolean isBurning() {
        boolean flag = this.world != null && this.world.isClientSide;
        return !this.fireProof && (this.fireTicks > 0 || flag && this.g(0));
    }

    public boolean au() {
        return this.vehicle != null;
    }

    public boolean isSneaking() {
        return this.g(1);
    }

    public void setSneaking(boolean flag) {
        this.b(1, flag);
    }

    public boolean isSprinting() {
        return this.g(3);
    }

    public void setSprinting(boolean flag) {
        this.b(3, flag);
    }

    public boolean isInvisible() {
        return this.g(5);
    }

    public void setInvisible(boolean flag) {
        this.b(5, flag);
    }

    public void f(boolean flag) {
        this.b(4, flag);
    }

    protected boolean g(int i) {
        return (this.datawatcher.getByte(0) & 1 << i) != 0;
    }

    protected void b(int i, boolean flag) {
        byte b0 = this.datawatcher.getByte(0);
        if(flag) {
            this.datawatcher.watch(0, Byte.valueOf((byte)(b0 | 1 << i)));
        } else {
            this.datawatcher.watch(0, Byte.valueOf((byte)(b0 & ~(1 << i))));
        }

    }

    public int getAirTicks() {
        return this.datawatcher.getShort(1);
    }

    public void setAirTicks(int i) {
        this.datawatcher.watch(1, Short.valueOf((short)i));
    }

    public void onLightningStrike(EntityLightning entitylightning) {
        CraftEntity thisBukkitEntity = this.getBukkitEntity();
        CraftEntity stormBukkitEntity = entitylightning.getBukkitEntity();
        PluginManager pluginManager = Bukkit.getPluginManager();
        if(thisBukkitEntity instanceof Hanging) {
            HangingBreakByEntityEvent entityCombustEvent = new HangingBreakByEntityEvent((Hanging)thisBukkitEntity, stormBukkitEntity);
            PaintingBreakByEntityEvent paintingEvent = null;
            if(thisBukkitEntity instanceof Painting) {
                paintingEvent = new PaintingBreakByEntityEvent((Painting)thisBukkitEntity, stormBukkitEntity);
            }

            pluginManager.callEvent(entityCombustEvent);
            if(paintingEvent != null) {
                paintingEvent.setCancelled(entityCombustEvent.isCancelled());
                pluginManager.callEvent(paintingEvent);
            }

            if(entityCombustEvent.isCancelled() || paintingEvent != null && paintingEvent.isCancelled()) {
                return;
            }
        }

        if(!this.fireProof) {
            CraftEventFactory.entityDamage = entitylightning;
            if(!this.damageEntity(DamageSource.LIGHTNING, 5.0F)) {
                CraftEventFactory.entityDamage = null;
            } else {
                ++this.fireTicks;
                if(this.fireTicks == 0) {
                    EntityCombustByEntityEvent var7 = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8);
                    pluginManager.callEvent(var7);
                    if(!var7.isCancelled()) {
                        this.setOnFire(var7.getDuration());
                    }
                }

            }
        }
    }

    public void a(EntityLiving entityliving) {
    }

    protected boolean j(double d0, double d1, double d2) {
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        double d3 = d0 - (double)blockposition.getX();
        double d4 = d1 - (double)blockposition.getY();
        double d5 = d2 - (double)blockposition.getZ();
        List list = this.world.a(this.getBoundingBox());
        if(list.isEmpty() && !this.world.u(blockposition)) {
            return false;
        } else {
            byte b0 = 3;
            double d6 = 9999.0D;
            if(!this.world.u(blockposition.west()) && d3 < d6) {
                d6 = d3;
                b0 = 0;
            }

            if(!this.world.u(blockposition.east()) && 1.0D - d3 < d6) {
                d6 = 1.0D - d3;
                b0 = 1;
            }

            if(!this.world.u(blockposition.up()) && 1.0D - d4 < d6) {
                d6 = 1.0D - d4;
                b0 = 3;
            }

            if(!this.world.u(blockposition.north()) && d5 < d6) {
                d6 = d5;
                b0 = 4;
            }

            if(!this.world.u(blockposition.south()) && 1.0D - d5 < d6) {
                d6 = 1.0D - d5;
                b0 = 5;
            }

            float f = this.random.nextFloat() * 0.2F + 0.1F;
            if(b0 == 0) {
                this.motX = (double)(-f);
            }

            if(b0 == 1) {
                this.motX = (double)f;
            }

            if(b0 == 3) {
                this.motY = (double)f;
            }

            if(b0 == 4) {
                this.motZ = (double)(-f);
            }

            if(b0 == 5) {
                this.motZ = (double)f;
            }

            return true;
        }
    }

    public void aA() {
        this.H = true;
        this.fallDistance = 0.0F;
    }

    public String getName() {
        if(this.hasCustomName()) {
            return this.getCustomName();
        } else {
            String s = EntityTypes.b(this);
            if(s == null) {
                s = "generic";
            }

            return LocaleI18n.get("entity." + s + ".name");
        }
    }

    public Entity[] aB() {
        return null;
    }

    public boolean k(Entity entity) {
        return this == entity;
    }

    public float getHeadRotation() {
        return 0.0F;
    }

    public void f(float f) {
    }

    public void g(float f) {
    }

    public boolean aD() {
        return true;
    }

    public boolean l(Entity entity) {
        return false;
    }

    public String toString() {
        return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[]{this.getClass().getSimpleName(), this.getName(), Integer.valueOf(this.id), this.world == null?"~NULL~":this.world.getWorldData().getName(), Double.valueOf(this.locX), Double.valueOf(this.locY), Double.valueOf(this.locZ)});
    }

    public boolean isInvulnerable(DamageSource damagesource) {
        return this.invulnerable && damagesource != DamageSource.OUT_OF_WORLD && !damagesource.u();
    }

    public void m(Entity entity) {
        this.setPositionRotation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
    }

    public void n(Entity entity) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        entity.e(nbttagcompound);
        this.f(nbttagcompound);
        this.portalCooldown = entity.portalCooldown;
        this.an = entity.an;
        this.ao = entity.ao;
        this.ap = entity.ap;
    }

    public void c(int i) {
        if(!this.world.isClientSide && !this.dead) {
            this.world.methodProfiler.a("changeDimension");
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            WorldServer exitWorld = null;
            if(this.dimension < 10) {
                Iterator exit = minecraftserver.worlds.iterator();

                while(exit.hasNext()) {
                    WorldServer enter = (WorldServer)exit.next();
                    if(enter.dimension == i) {
                        exitWorld = enter;
                    }
                }
            }

            Location enter1 = this.getBukkitEntity().getLocation();
            Location exit1 = exitWorld != null?minecraftserver.getPlayerList().calculateTarget(enter1, minecraftserver.getWorldServer(i)):null;
            boolean useTravelAgent = exitWorld != null && (this.dimension != 1 || exitWorld.dimension != 1);
            TravelAgent agent = exit1 != null?(TravelAgent)((CraftWorld)exit1.getWorld()).getHandle().getTravelAgent():CraftTravelAgent.DEFAULT;
            EntityPortalEvent event = new EntityPortalEvent(this.getBukkitEntity(), enter1, exit1, agent);
            event.useTravelAgent(useTravelAgent);
            event.getEntity().getServer().getPluginManager().callEvent(event);
            if(event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null || !this.isAlive()) {
                return;
            }

            exit1 = event.useTravelAgent()?event.getPortalTravelAgent().findOrCreate(event.getTo()):event.getTo();
            this.teleportTo(exit1, true);
        }

    }

    public void teleportTo(Location exit, boolean portal) {
        WorldServer worldserver = ((CraftWorld)this.getBukkitEntity().getLocation().getWorld()).getHandle();
        WorldServer worldserver1 = ((CraftWorld)exit.getWorld()).getHandle();
        int i = worldserver1.dimension;
        this.dimension = i;
        this.world.kill(this);
        this.dead = false;
        this.world.methodProfiler.a("reposition");
        boolean before = worldserver1.chunkProviderServer.forceChunkLoad;
        worldserver1.chunkProviderServer.forceChunkLoad = true;
        worldserver1.getMinecraftServer().getPlayerList().repositionEntity(this, exit, portal);
        worldserver1.chunkProviderServer.forceChunkLoad = before;
        this.world.methodProfiler.c("reloading");
        Entity entity = EntityTypes.createEntityByName(EntityTypes.b(this), worldserver1);
        if(entity != null) {
            entity.n(this);
            worldserver1.addEntity(entity);
            this.getBukkitEntity().setHandle(entity);
            entity.bukkitEntity = this.getBukkitEntity();
            if(this instanceof EntityInsentient) {
                ((EntityInsentient)this).unleash(true, false);
            }
        }

        this.dead = true;
        this.world.methodProfiler.b();
        worldserver.j();
        worldserver1.j();
        this.world.methodProfiler.b();
    }

    public float a(Explosion explosion, World world, BlockPosition blockposition, IBlockData iblockdata) {
        return iblockdata.getBlock().a(this);
    }

    public boolean a(Explosion explosion, World world, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return true;
    }

    public int aE() {
        return 3;
    }

    public Vec3D aG() {
        return this.ao;
    }

    public EnumDirection aH() {
        return this.ap;
    }

    public boolean aI() {
        return false;
    }

    public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Entity Type", new Callable() {
            public String a() throws Exception {
                return EntityTypes.b(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Entity ID", Integer.valueOf(this.id));
        crashreportsystemdetails.a("Entity Name", new Callable() {
            public String a() throws Exception {
                return Entity.this.getName();
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Entity\'s Exact location", String.format("%.2f, %.2f, %.2f", new Object[]{Double.valueOf(this.locX), Double.valueOf(this.locY), Double.valueOf(this.locZ)}));
        crashreportsystemdetails.a("Entity\'s Block location", CrashReportSystemDetails.a((double)MathHelper.floor(this.locX), (double)MathHelper.floor(this.locY), (double)MathHelper.floor(this.locZ)));
        crashreportsystemdetails.a("Entity\'s Momentum", String.format("%.2f, %.2f, %.2f", new Object[]{Double.valueOf(this.motX), Double.valueOf(this.motY), Double.valueOf(this.motZ)}));
        crashreportsystemdetails.a("Entity\'s Rider", new Callable() {
            public String a() throws Exception {
                return Entity.this.passenger.toString();
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Entity\'s Vehicle", new Callable() {
            public String a() throws Exception {
                return Entity.this.vehicle.toString();
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public boolean aL() {
        return true;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        ChatComponentText chatcomponenttext = new ChatComponentText(this.getName());
        chatcomponenttext.getChatModifier().setChatHoverable(this.aQ());
        chatcomponenttext.getChatModifier().setInsertion(this.getUniqueID().toString());
        return chatcomponenttext;
    }

    public void setCustomName(String s) {
        this.datawatcher.watch(2, s);
    }

    public String getCustomName() {
        return this.datawatcher.getString(2);
    }

    public boolean hasCustomName() {
        return this.datawatcher.getString(2).length() > 0;
    }

    public void setCustomNameVisible(boolean flag) {
        this.datawatcher.watch(3, Byte.valueOf((byte)(flag?1:0)));
    }

    public boolean getCustomNameVisible() {
        return this.datawatcher.getByte(3) == 1;
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
    }

    public void i(int i) {
    }

    public EnumDirection getDirection() {
        return EnumDirection.fromType2(MathHelper.floor((double)(this.yaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    protected ChatHoverable aQ() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        String s = EntityTypes.b(this);
        nbttagcompound.setString("id", this.getUniqueID().toString());
        if(s != null) {
            nbttagcompound.setString("type", s);
        }

        nbttagcompound.setString("name", this.getName());
        return new ChatHoverable(EnumHoverAction.SHOW_ENTITY, new ChatComponentText(nbttagcompound.toString()));
    }

    public boolean a(EntityPlayer entityplayer) {
        return true;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void a(AxisAlignedBB axisalignedbb) {
        double a = axisalignedbb.a;
        double b = axisalignedbb.b;
        double c = axisalignedbb.c;
        double d = axisalignedbb.d;
        double e = axisalignedbb.e;
        double f = axisalignedbb.f;
        double len = axisalignedbb.d - axisalignedbb.a;
        if(len < 0.0D) {
            d = a;
        }

        if(len > 64.0D) {
            d = a + 64.0D;
        }

        len = axisalignedbb.e - axisalignedbb.b;
        if(len < 0.0D) {
            e = b;
        }

        if(len > 64.0D) {
            e = b + 64.0D;
        }

        len = axisalignedbb.f - axisalignedbb.c;
        if(len < 0.0D) {
            f = c;
        }

        if(len > 64.0D) {
            f = c + 64.0D;
        }

        this.boundingBox = new AxisAlignedBB(a, b, c, d, e, f);
    }

    public float getHeadHeight() {
        return this.length * 0.85F;
    }

    public boolean aT() {
        return this.g;
    }

    public void h(boolean flag) {
        this.g = flag;
    }

    public boolean d(int i, ItemStack itemstack) {
        return false;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
    }

    public boolean a(int i, String s) {
        return true;
    }

    public BlockPosition getChunkCoordinates() {
        return new BlockPosition(this.locX, this.locY + 0.5D, this.locZ);
    }

    public Vec3D d() {
        return new Vec3D(this.locX, this.locY, this.locZ);
    }

    public World getWorld() {
        return this.world;
    }

    public Entity f() {
        return this;
    }

    public boolean getSendCommandFeedback() {
        return false;
    }

    public void a(EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
        this.au.a(this, commandobjectiveexecutor_enumcommandresult, i);
    }

    public CommandObjectiveExecutor aU() {
        return this.au;
    }

    public void o(Entity entity) {
        this.au.a(entity.aU());
    }

    public NBTTagCompound getNBTTag() {
        return null;
    }

    public boolean a(EntityHuman entityhuman, Vec3D vec3d) {
        return false;
    }

    public boolean aW() {
        return false;
    }

    protected void a(EntityLiving entityliving, Entity entity) {
        if(entity instanceof EntityLiving) {
            EnchantmentManager.a((EntityLiving)entity, entityliving);
        }

        EnchantmentManager.b(entityliving, entity);
    }
}
     */
}