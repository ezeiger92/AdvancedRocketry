package zmaster587.advancedRocketry.client.render.planet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.util.Vector3F;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RenderPlanetarySky extends IRenderHandler {


	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;
	ResourceLocation currentlyBoundTex = null;
	float celestialAngle;
	Vector3F<Float> axis;

	//Mostly vanilla code
	//TODO: make usable on other planets
	public RenderPlanetarySky() {
		axis = new Vector3F<Float>(1f, 0f, 0f);

		this.starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		this.glSkyList = this.starGLCallList + 1;
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		byte b2 = 64;
		int i = 256 / b2 + 2;
		float f = 16.0F;
		int j;
		int k;

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
				buffer.pos((double)(j + 0), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + b2)).endVertex();
				buffer.pos((double)(j + 0), (double)f, (double)(k + b2)).endVertex();
				Tessellator.getInstance().draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.starGLCallList + 2;
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		f = -16.0F;
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				buffer.pos((double)(j + 0), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + b2)).endVertex();
				buffer.pos((double)(j + 0), (double)f, (double)(k + b2)).endVertex();
			}
		}

		Tessellator.getInstance().draw();
		GL11.glEndList();
	}

	Minecraft mc = Minecraft.getMinecraft();

	private void renderStars()
	{
		Random random = new Random(10842L);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		for (int i = 0; i < 2000; ++i)
		{
			double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d4 < 1.0D && d4 > 0.01D)
			{
				d4 = 1.0D / Math.sqrt(d4);
				d0 *= d4;
				d1 *= d4;
				d2 *= d4;
				double d5 = d0 * 100.0D;
				double d6 = d1 * 100.0D;
				double d7 = d2 * 100.0D;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d15 = Math.sin(d14);
				double d16 = Math.cos(d14);

				for (int j = 0; j < 4; ++j)
				{
					double d17 = 0.0D;
					double d18 = (double)((j & 2) - 1) * d3;
					double d19 = (double)((j + 1 & 2) - 1) * d3;
					double d20 = d18 * d16 - d19 * d15;
					double d21 = d19 * d16 + d18 * d15;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					buffer.pos(d5 + d24, d6 + d22, d7 + d25).endVertex();
				}
			}
		}			

		Tessellator.getInstance().draw();
		//buffer.finishDrawing();
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {

		//TODO: properly handle this
		float atmosphere;
		int solarOrbitalDistance, planetOrbitalDistance = 0;
		double myPhi = 0, myTheta = 0, myPrevOrbitalTheta = 0, myRotationalPhi = 0;
		boolean hasAtmosphere = false, isMoon;
		float parentAtmColor[] = new float[]{1f,1f,1f};
		float parentRingColor[] = new float[] {1f,1f,1f};
		float ringColor[] = new float[] {1f,1f,1f};
		float sunSize = 1.0f;
		float starSeperation = 0f;
		boolean isWarp = false;
		boolean isGasGiant = false;
		boolean hasRings = false;
		boolean parentPlanetHasDecorator = true;
		boolean parentHasRings = false;
		DimensionProperties parentProperties = null;
		DimensionProperties properties;
		EnumFacing travelDirection = null;
		ResourceLocation parentPlanetIcon = null;
		List<DimensionProperties> children;
		StellarBody primaryStar = DimensionManager.getSol();
		List<StellarBody> subStars = new LinkedList<StellarBody>();
		celestialAngle = mc.world.getCelestialAngle(partialTicks);

		Vec3d sunColor;


		if(mc.world.provider instanceof IPlanetaryProvider) {
			IPlanetaryProvider planetaryProvider = (IPlanetaryProvider)mc.world.provider;

			properties = (DimensionProperties)planetaryProvider.getDimensionProperties(mc.player.getPosition());

			atmosphere = planetaryProvider.getAtmosphereDensityFromHeight(mc.getRenderViewEntity().posY, mc.player.getPosition());
			EnumFacing dir = getRotationAxis(properties, mc.player.getPosition());
			axis.x = (float) dir.getFrontOffsetX();
			axis.y = (float) dir.getFrontOffsetY();
			axis.z = (float) dir.getFrontOffsetZ();

			myPhi = properties.getOrbitalPhi();
			myTheta = properties.getOrbitTheta();
			myRotationalPhi = properties.rotationalPhi;
			myPrevOrbitalTheta = properties.getPreviousOrbitTheta();
			hasRings = properties.hasRings();
			ringColor = properties.ringColor;

			children = new LinkedList<DimensionProperties>();
			for (Integer i : properties.getChildPlanets()) {
				children.add(DimensionManager.getInstance().getDimensionProperties(i));
			}

			solarOrbitalDistance = properties.getSolarOrbitalDistance();


			if(isMoon = properties.isMoon()) {
				parentProperties = properties.getParentProperties();
				isGasGiant = parentProperties.isGasGiant();
				hasAtmosphere = parentProperties.hasAtmosphere();
				planetOrbitalDistance = properties.getParentOrbitalDistance();
				parentAtmColor = parentProperties.skyColor;
				parentPlanetIcon = getTextureForPlanet(parentProperties);
				parentHasRings = parentProperties.hasRings;
				parentRingColor = parentProperties.ringColor;
				parentPlanetHasDecorator = parentProperties.hasDecorators();
			}

			sunColor = planetaryProvider.getSunColor(mc.player.getPosition());
			primaryStar = properties.getStar();
			if (primaryStar != null)
			{
				sunSize = properties.getStar().getSize();
				subStars = properties.getStar().getSubStars();
				starSeperation = properties.getStar().getStarSeperation();
			}
			if(world.provider.getDimension() == Configuration.spaceDimId) {
				isWarp = properties.getParentPlanet() == SpaceObjectManager.WARPDIMID;
				if(isWarp) {
					SpaceObject station = (SpaceObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(mc.player.getPosition());
					travelDirection = station.getForwardDirection();
				}
			}
		}
		else if(DimensionManager.getInstance().isDimensionCreated(mc.world.provider.getDimension())) {

			properties = DimensionManager.getInstance().getDimensionProperties(mc.world.provider.getDimension());

			atmosphere = properties.getAtmosphereDensityAtHeight(mc.getRenderViewEntity().posY);//planetaryProvider.getAtmosphereDensityFromHeight(mc.getRenderViewEntity().posY, mc.player.getPosition());
			EnumFacing dir = getRotationAxis(properties, mc.player.getPosition());
			axis.x = (float) dir.getFrontOffsetX();
			axis.y = (float) dir.getFrontOffsetY();
			axis.z = (float) dir.getFrontOffsetZ();

			myPhi = properties.getOrbitalPhi();
			myTheta = properties.getOrbitTheta();
			myRotationalPhi = properties.rotationalPhi;
			myPrevOrbitalTheta = properties.getPreviousOrbitTheta();
			hasRings = properties.hasRings();
			ringColor = properties.ringColor;

			children = new LinkedList<DimensionProperties>();
			for (Integer i : properties.getChildPlanets()) {
				children.add(DimensionManager.getInstance().getDimensionProperties(i));
			}

			solarOrbitalDistance = properties.getSolarOrbitalDistance();


			if(isMoon = properties.isMoon()) {
				parentProperties = properties.getParentProperties();
				isGasGiant = parentProperties.isGasGiant();
				hasAtmosphere = parentProperties.hasAtmosphere();
				planetOrbitalDistance = properties.getParentOrbitalDistance();
				parentAtmColor = parentProperties.skyColor;
				parentPlanetIcon = getTextureForPlanet(parentProperties);
				parentHasRings = parentProperties.hasRings;
				parentRingColor = parentProperties.ringColor;
			}

			float sunColorFloat[] = properties.getSunColor();
			sunColor = new Vec3d(sunColorFloat[0], sunColorFloat[1], sunColorFloat[2]);//planetaryProvider.getSunColor(mc.player.getPosition());
			primaryStar = properties.getStar();
			if (primaryStar != null)
			{
				sunSize = properties.getStar().getSize();
				subStars = properties.getStar().getSubStars();
				starSeperation = properties.getStar().getStarSeperation();
			}
			if(world.provider.getDimension() == Configuration.spaceDimId) {
				isWarp = properties.getParentPlanet() == SpaceObjectManager.WARPDIMID;
				if(isWarp) {
					SpaceObject station = (SpaceObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(mc.player.getPosition());
					travelDirection = station.getForwardDirection();
				}
			}
		}
		else {
			children = new LinkedList<DimensionProperties>();
			isMoon = false;
			hasAtmosphere = DimensionManager.overworldProperties.hasAtmosphere();
			atmosphere = DimensionManager.overworldProperties.getAtmosphereDensityAtHeight(mc.getRenderViewEntity().posY);
			solarOrbitalDistance = DimensionManager.overworldProperties.getOrbitalDist();
			sunColor = new Vec3d(1, 1, 1);
			primaryStar = DimensionManager.overworldProperties.getStar();
			properties = DimensionManager.overworldProperties;
		}

		GlStateManager.disableTexture2D();
		Vec3d skyColor = Minecraft.getMinecraft().world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
		float skyColorR = (float)skyColor.x;
		float skyColorG = (float)skyColor.y;
		float skyColorB = (float)skyColor.z;

		if (this.mc.gameSettings.anaglyph)
		{
			float anaglyphColorR = (skyColorR * 30.0F + skyColorG * 59.0F + skyColorB * 11.0F) / 100.0F;
			float anaglyphColorG = (skyColorR * 30.0F + skyColorG * 70.0F) / 100.0F;
			float anaglyphColorB = (skyColorR * 30.0F + skyColorB * 70.0F) / 100.0F;
			skyColorR = anaglyphColorR;
			skyColorG = anaglyphColorG;
			skyColorB = anaglyphColorB;
		}

		//Simulate atmospheric thickness
		skyColorR *= atmosphere;
		skyColorG *= atmosphere;
		skyColorB *= atmosphere;

		GlStateManager.color(skyColorR, skyColorG, skyColorB);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		GlStateManager.depthMask(false);
		GlStateManager.enableFog();
		GlStateManager.color(skyColorR, skyColorG, skyColorB);
		GL11.glCallList(this.glSkyList);
		GlStateManager.disableFog();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		float[] sunriseColor = mc.world.provider.calcSunriseSunsetColors(celestialAngle, partialTicks);

		if (sunriseColor != null)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GL11.glPushMatrix();
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(MathHelper.sin(mc.world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotated(90.0F - Math.toDegrees(myRotationalPhi), 0.0F, 0.0F, 1.0F);

			//Sim atmospheric thickness
			float sunriseColorR = sunriseColor[0];
			float sunriseColorG = sunriseColor[1];
			float sunriseColorB = sunriseColor[2];

			if (this.mc.gameSettings.anaglyph)
			{
				float anaglyphColorR = (sunriseColorR * 30.0F + sunriseColorG * 59.0F + sunriseColorB * 11.0F) / 100.0F;
				float anaglyphColorG = (sunriseColorR * 30.0F + sunriseColorG * 70.0F) / 100.0F;
				float anaglyphColorB = (sunriseColorR * 30.0F + sunriseColorB * 70.0F) / 100.0F;
				sunriseColorR = anaglyphColorR;
				sunriseColorG = anaglyphColorG;
				sunriseColorB = anaglyphColorB;
			}

			buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(0.0D, 100.0D, 0.0D).color(sunriseColorR, sunriseColorG, sunriseColorB, sunriseColor[3] * atmosphere).endVertex();
			byte b0 = 16;

			for (int j = 0; j <= b0; ++j)
			{
				float f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
				float f12 = MathHelper.sin(f11);
				float f13 = MathHelper.cos(f11);
				buffer.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * sunriseColor[3])).color(sunriseColor[0], sunriseColor[1], sunriseColor[2], 0.0F).endVertex();
			}

			Tessellator.getInstance().draw();
			GL11.glPopMatrix();
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		GlStateManager.enableTexture2D();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		GL11.glPushMatrix();

		float skyVisibility;
		if(atmosphere > 0)
			skyVisibility = 1.0F - (mc.world.getRainStrength(partialTicks)*atmosphere);
		else
			skyVisibility = 1f;

		GlStateManager.color(1.0F, 1.0F, 1.0F, skyVisibility);
		GL11.glTranslatef(0.0F, 0.0F, 0.0F);
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);

		float multiplier = (2-atmosphere)/2f * skyVisibility;//atmosphere > 1 ? (2-atmosphere) : 1f;

		GL11.glRotatef((float)Math.toDegrees(myRotationalPhi), 0f, 1f, 0f);

		//Draw Rings
		if(hasRings) {
			GL11.glPushMatrix();
			GL11.glRotatef(90f, 0f, 1f, 0f);

			float f10 = 100;
			double ringDist = 0;
			mc.renderEngine.bindTexture(DimensionProperties.planetRings);

			GL11.glRotated(70, 1, 0, 0);
			GL11.glTranslated(0, -10, 0);

			GlStateManager.color(ringColor[0], ringColor[1], ringColor[2],multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
			buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GL11.glPushMatrix();

			GL11.glRotatef(90f, 0f, 1f, 0f);
			GL11.glRotated(70, 1, 0, 0);
			GL11.glRotatef(isWarp ? 0 : celestialAngle * 360.0F, 0, 1, 0);
			GL11.glTranslated(0, -10, 0);



			mc.renderEngine.bindTexture(DimensionProperties.planetRingShadow);
			GlStateManager.color(0f, 0f, 0f,multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
			buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		}

		if(!isWarp)
			rotateAroundAxis();


		GlStateManager.disableTexture2D();
		float starVisibility = mc.world.getStarBrightness(partialTicks) * skyVisibility * (atmosphere) + (1-atmosphere);

		if (starVisibility > 0.0F)
		{
			GlStateManager.color(starVisibility, starVisibility, starVisibility, starVisibility);
			GL11.glPushMatrix();
			if(isWarp) {
				for(int i = -3; i < 5; i++) {
					GL11.glPushMatrix();
					double magnitude = i*-100 + (((System.currentTimeMillis()) + 50) % 2000)/20f;
					GL11.glTranslated(-travelDirection.getFrontOffsetZ()*magnitude, 0, travelDirection.getFrontOffsetX()*magnitude);
					GL11.glCallList(this.starGLCallList);
					GL11.glPopMatrix();
				}
				//GL11.glTranslated(((System.currentTimeMillis()/10) + 50) % 100, 0, 0);
			}
			else {
				GL11.glCallList(this.starGLCallList);
				//Extra stars for low ATM
				if(atmosphere < 0.5) {
					GlStateManager.color(starVisibility, starVisibility, starVisibility, starVisibility/2f);
					GL11.glPushMatrix();
					GL11.glRotatef(-90, 0, 1, 0);
					GL11.glCallList(this.starGLCallList);
					GL11.glPopMatrix();
				}
				if(atmosphere < 0.25) {
					GlStateManager.color(starVisibility, starVisibility, starVisibility, starVisibility/4f);
					GL11.glPushMatrix();
					GL11.glRotatef(90, 0, 1, 0);
					GL11.glCallList(this.starGLCallList);
					GL11.glPopMatrix();
				}
				GlStateManager.color(starVisibility, starVisibility, starVisibility, starVisibility);
			}
			GL11.glPopMatrix();
		}
		GlStateManager.enableTexture2D();

		//--------------------------- Draw the suns --------------------
		if(!isWarp) {
			//Set sun color and distance
			drawStar(buffer, primaryStar, properties, solarOrbitalDistance, sunSize, sunColor, multiplier);

			if(subStars != null && !subStars.isEmpty()) {
				GL11.glPushMatrix();
				float phaseInc = 360/subStars.size();

				for(StellarBody subStar : subStars) {
					GL11.glRotatef(phaseInc, 0, 1, 0);
					GL11.glPushMatrix();

					GL11.glRotatef(subStar.getStarSeperation()*(202-solarOrbitalDistance)/100f, 1, 0, 0);
					float color[] = subStar.getColor();
					drawStar(buffer, subStar , properties, solarOrbitalDistance, subStar.getSize(), new Vec3d(color[0], color[1], color[2]), multiplier);
					GL11.glPopMatrix();
				}
				GL11.glPopMatrix();
			}

		}

		//Render the parent planet
		if(isMoon) {
			GL11.glPushMatrix();

			GL11.glRotatef((float)Math.toDegrees(myPhi), 0f, 0f, 1f);
			
			GL11.glRotatef((float)Math.toDegrees(partialTicks * myTheta + (1 - partialTicks) * myPrevOrbitalTheta), 1f, 0f, 0f);

			float phiAngle = (float)myPhi;

			//Close enough approximation, I missed something but seems to off by no more than 30*
			//Nobody will look
			double x = MathHelper.sin(phiAngle)*MathHelper.cos((float)myTheta);
			double y = -MathHelper.sin((float)myTheta);
			double rotation = -Math.PI/2f + Math.atan2(x, y) - (myTheta - Math.PI )*MathHelper.sin(phiAngle);

			//Draw Rings
			if(parentHasRings) {
				GL11.glPushMatrix();
				GL11.glRotatef(90f, 0f, 1f, 0f);

				double ringBound = 100;
				double ringDist = 0;
				mc.renderEngine.bindTexture(DimensionProperties.planetRings);

				GL11.glRotated(70, 1, 0, 0);
				GL11.glTranslated(0, -10, 50);

				GlStateManager.color(parentRingColor[0], parentRingColor[1], parentRingColor[2],multiplier);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				buffer.pos( ringBound, ringDist, -ringBound).tex(1.0D, 0.0D).endVertex();
				buffer.pos(-ringBound, ringDist, -ringBound).tex(0.0D, 0.0D).endVertex();
				buffer.pos(-ringBound, ringDist,  ringBound).tex(0.0D, 1.0D).endVertex();
				buffer.pos( ringBound, ringDist,  ringBound).tex(1.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();

				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glPushMatrix();

				GL11.glRotatef(90f, 0f, 1f, 0f);
				GL11.glRotated(70, 1, 0, 0);
				GL11.glTranslated(0, -10, 50);

				mc.renderEngine.bindTexture(DimensionProperties.planetRingShadow);
				GlStateManager.color(0f, 0f, 0f,1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				buffer.pos( ringBound, ringDist, -ringBound).tex(1.0D, 0.0D).endVertex();
				buffer.pos(-ringBound, ringDist, -ringBound).tex(0.0D, 0.0D).endVertex();
				buffer.pos(-ringBound, ringDist,  ringBound).tex(0.0D, 1.0D).endVertex();
				buffer.pos( ringBound, ringDist,  ringBound).tex(1.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();

				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			}

			assert(parentProperties != null);
			renderPlanet(buffer, parentProperties, planetOrbitalDistance, multiplier, rotation);
			GL11.glPopMatrix();
		}

		for(DimensionProperties moon : children) {
			GL11.glPushMatrix();

			double rot = Math.toDegrees(partialTicks*moon.getOrbitTheta() + (1 - partialTicks) * moon.getPreviousOrbitTheta());

			GL11.glRotatef((float)Math.toDegrees(moon.getOrbitalPhi()), 0f, 0f, 1f);
			GL11.glRotated(rot, 1f, 0f, 0f);

			//Close enough approximation, I missed something but seems to off by no more than 30*
			//Nobody will look
			// I looked, and you missed mixing radians and degrees. <3 -Erik
			float phiAngle = (float)moon.getOrbitalPhi();
			double x = -MathHelper.sin(phiAngle)*MathHelper.cos((float)moon.getOrbitTheta());
			double y = MathHelper.sin((float)moon.getOrbitTheta());
			double rotation = -Math.PI/2f + Math.atan2(x, y) - (moon.getOrbitTheta() - Math.PI)*MathHelper.sin(phiAngle);

			renderPlanet(buffer, moon, moon.getParentOrbitalDistance(), multiplier, rotation);
			GL11.glPopMatrix();
		}

		GlStateManager.enableFog();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();

		GL11.glPopMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.color(0.0F, 0.0F, 0.0F);
		double playerDepth = this.mc.player.getPositionEyes(partialTicks).y - mc.world.getHorizon();

		if (playerDepth < 0.0D)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 12.0F, 0.0F);
			GL11.glCallList(this.glSkyList2);
			GL11.glPopMatrix();
			double bound = 1.0F;
			double f9 = -((float)(playerDepth + 65.0D));
			double f10 = -bound;
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

			// TODO: Figure out what this is
			buffer.color(0,0,0,1f);
			buffer.pos(-bound, f9,   bound).endVertex();
			buffer.pos( bound, f9,   bound).endVertex();
			buffer.pos( bound, f10,  bound).endVertex();
			buffer.pos(-bound, f10,  bound).endVertex();
			buffer.pos(-bound, f10, -bound).endVertex();
			buffer.pos( bound, f10, -bound).endVertex();
			buffer.pos( bound, f9,  -bound).endVertex();
			buffer.pos(-bound, f9,  -bound).endVertex();
			buffer.pos( bound, f10, -bound).endVertex();
			buffer.pos( bound, f10,  bound).endVertex();
			buffer.pos( bound, f9,   bound).endVertex();
			buffer.pos( bound, f9,  -bound).endVertex();
			buffer.pos(-bound, f9,  -bound).endVertex();
			buffer.pos(-bound, f9,   bound).endVertex();
			buffer.pos(-bound, f10,  bound).endVertex();
			buffer.pos(-bound, f10, -bound).endVertex();
			buffer.pos(-bound, f10, -bound).endVertex();
			buffer.pos(-bound, f10,  bound).endVertex();
			buffer.pos( bound, f10,  bound).endVertex();
			buffer.pos( bound, f10, -bound).endVertex();

			Tessellator.getInstance().draw();
		}

		if (mc.world.provider.isSkyColored())
		{
			GlStateManager.color(skyColorR * 0.2F + 0.04F, skyColorG * 0.2F + 0.04F, skyColorB * 0.6F + 0.1F);
		}
		else
		{
			GlStateManager.color(skyColorR, skyColorG, skyColorB);
		}

		//Blackness @ bottom of world
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, -((float)(playerDepth - 16.0D)), 0.0F);
		GL11.glCallList(this.glSkyList2);
		GL11.glPopMatrix();

		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);

		RocketEventHandler.onPostWorldRender(partialTicks);
		//Fix player/items going transparent
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	protected ResourceLocation getTextureForPlanet(DimensionProperties properties) {
		return properties.getPlanetIcon();
	}

	protected ResourceLocation getTextureForPlanetLEO(DimensionProperties properties) {
		return properties.getPlanetIcon();
	}

	protected EnumFacing getRotationAxis(DimensionProperties properties, BlockPos pos) {
		return EnumFacing.EAST;
	}

	protected void renderPlanet(BufferBuilder buffer, DimensionProperties properties, float distance, float alphaMultiplier, double shadowAngle) {
		ResourceLocation icon = getTextureForPlanet(properties);
		boolean hasAtmosphere = properties.hasAtmosphere();
		boolean gasGiant = properties.isGasGiant();
		boolean hasRing = properties.hasRings();
		boolean hasDecorators = properties.hasDecorators();
		float skyColor[] = properties.skyColor;
		float ringColor[] = properties.skyColor;
		
		float size = properties.getSize() / distance * 1000;

		renderPlanetPubHelper(buffer, icon, 0, 0, 20, size, alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing, hasDecorators);
	}

	protected void rotateAroundAxis() {
		Vector3F<Float> axis = getRotateAxis();
		GL11.glRotatef(getSkyRotationAmount() * 360.0F, axis.x, axis.y, axis.z);
	}

	protected float getSkyRotationAmount() {
		return celestialAngle;
	}

	protected Vector3F<Float> getRotateAxis() {
		return axis;
	}
	
	public static void bufferPutSquare(BufferBuilder buffer, double radius, double depth) {
		buffer.pos(-radius, depth, -radius).tex(0, 0).endVertex();
		buffer.pos( radius, depth, -radius).tex(1, 0).endVertex();
		buffer.pos( radius, depth,  radius).tex(1, 1).endVertex();
		buffer.pos(-radius, depth,  radius).tex(0, 1).endVertex();
	}

	public static void renderPlanetPubHelper(BufferBuilder buffer, ResourceLocation icon, int locationX, int locationY, double zLevel, float size, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing, boolean hasDecorators) {
		GlStateManager.enableBlend();

		GL11.glPushMatrix();
		GL11.glTranslated(locationX, zLevel, locationY);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(icon);
		// Mask behind planet to block out stars
		{
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			
			float adjustedMultiplier = Math.min((float)Math.pow(alphaMultiplier, .75f) * 2.4f, 1.0f);

			// TODO: Sky color here
			GlStateManager.color(0f, 0f, 0f, adjustedMultiplier);
			
			bufferPutSquare(buffer, size, zLevel - 0.005f);
			Tessellator.getInstance().draw();
		}
		
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//TODO: draw sky planets

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		GlStateManager.color(1f, 1f, 1f, alphaMultiplier);
		
		bufferPutSquare(buffer, size, zLevel + 0.005f);
		Tessellator.getInstance().draw();
		//buffer.finishDrawing();

		//GL11.glEnable(GL11.GL_BLEND);
		
		if (hasDecorators) {
			//ATM Glow
			GL11.glPushMatrix();
			GL11.glRotated(90 + Math.toDegrees(shadowAngle), 0, 1, 0);

			//Rings
			if(hasRing) {
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(ringColor[0], ringColor[1], ringColor[2], alphaMultiplier*0.2f);
				float ringSize = size * 1.4f;
				
				Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.planetRings);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				
				bufferPutSquare(buffer, ringSize, zLevel + 0.01f);
				Tessellator.getInstance().draw();

				GlStateManager.color(0f, 0f, 0f, alphaMultiplier);
				Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.planetRingShadow);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				
				bufferPutSquare(buffer, ringSize, zLevel + 0.01f);
				Tessellator.getInstance().draw();
			}

			// Render bright crecent 
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.atmGlow);

			GlStateManager.color(1f, 1f, 1f, alphaMultiplier);
			
			bufferPutSquare(buffer, size, zLevel - 0.01f);
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();
		}

		//End ATM glow

		if (hasDecorators) {
			//Draw atmosphere if applicable
			if(hasAtmosphere) {
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.getAtmosphereResource());
				GlStateManager.color(skyColor[0], skyColor[1], skyColor[2], alphaMultiplier);
				
				bufferPutSquare(buffer, size, zLevel + 0.01f);
				Tessellator.getInstance().draw();
				//buffer.finishDrawing();

			}


			GL11.glRotated(90 + Math.toDegrees(shadowAngle), 0, 1, 0);

			//Draw Shadow
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.getShadowResource());
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1f, 1f, 1f, alphaMultiplier);
			
			// Slightly expand to cover up fuzzy edges (especially in planet selector)
			// A better idea is rendering to a texture, maybe another time
			bufferPutSquare(buffer, size * 1.006, zLevel + 0.01f);
			Tessellator.getInstance().draw();
		}

		GL11.glPopMatrix();


		GlStateManager.color(1f, 1f, 1f, 1f);
	}

	protected void drawStar(BufferBuilder buffer, StellarBody sun, DimensionProperties properties, int solarOrbitalDistance, float sunSize, Vec3d sunColor, float multiplier) {
		
		// Minimum orbital distance *should* prevent division by zero
		final double radius = sunSize / solarOrbitalDistance * 1000;
		
		// Yea, yea, I know it's not "Z", but that's how it's being treated.
		final double zLevel = 100.0;
		
		if(sun != null && sun.isBlackHole()) {
			//GlStateManager.depthMask(true);
			//GlStateManager.enableAlpha();
			//GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			mc.renderEngine.bindTexture(TextureResources.locationBlackHole);	

			GL11.glPushMatrix();
			GL11.glTranslatef(0, 100, 0);
			float phase = -(System.currentTimeMillis() % 3600)/3600f;
			//float scale = 1+(float)Math.sin(phase*3.14)*0.1f;
			phase*=360f;
			GL11.glRotatef(phase, 0, 1, 0);

			//GL11.glScaled(scale,scale,scale);

			//Set sun color and distance
			GlStateManager.color(.2f, .5f, .4f, 1f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			double f10 = 0.8 * radius;
			//multiplier = 2;
			
			bufferPutSquare(buffer, f10, zLevel);
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();
			
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			
			// TODO: This changes position slightly based on time of day; inspect
			//Render accretion disk
			mc.renderEngine.bindTexture(TextureResources.locationAccretionDisk);
			GlStateManager.depthMask(false);
			for(int i = 0; i < 2; i++)
			{
				float speedMult = (2 - i)*1.01f + 1;
				GL11.glPushMatrix();
				
				GL11.glTranslatef(0, (float)zLevel, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*36000))/(100f*speedMult), 0, 1, 0);

				GlStateManager.color((float)1, (float).5 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = 3.9 * radius + i / 1.4;
				
				bufferPutSquare(buffer, f10, 0);
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();

				GL11.glPushMatrix();

				GL11.glTranslatef(0, (float)zLevel - 0.01f, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*360*50))/(50f*speedMult), 0, 1, 0);

				GlStateManager.color((float)0.8, (float).7 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = 2.8 * radius + i / 2.2;
				//multiplier = 2;
				bufferPutSquare(buffer, f10, 0);
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();

				GL11.glPushMatrix();

				GL11.glTranslatef(0, (float)zLevel - 0.02f, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*360*25))/(25f*speedMult), 0, 1, 0);

				GlStateManager.color((float)0.2, (float).4 , (float)1 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = 2 * radius + i / 3;
				//multiplier = 2;
				bufferPutSquare(buffer, f10, 0);
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();
			}

		}
		else {
			mc.renderEngine.bindTexture(TextureResources.locationSunPng);
			//Set sun color and distance
			GlStateManager.color((float)sunColor.x, (float)sunColor.y , (float)sunColor.z, Math.min((multiplier)*2f, 1f));
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			
			bufferPutSquare(buffer, radius, zLevel);
			Tessellator.getInstance().draw();
		}
	}
}
