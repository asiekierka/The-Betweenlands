package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thebetweenlands.manual.gui.GuiManualBase;

import java.util.ArrayList;

/**
 * Created by Bart on 12-8-2015.
 */
public class TextWidget extends ManualWidgetsBase {
    public String text;
    public int color = 0x000000;
    public int unchangedColor = 0x000000;

    public TextWidget(GuiManualBase manual, int xStart, int yStart, String unlocalizedText) {
        super(manual, xStart, yStart);
        text = StatCollector.translateToLocal(unlocalizedText);
    }

    public TextWidget(GuiManualBase manual, int xStart, int yStart, String unlocalizedText, int color) {
        super(manual, xStart, yStart);
        text = StatCollector.translateToLocal(unlocalizedText);
        this.color = color;
        this.unchangedColor = color;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        int widthLine = 0;
        if (text != null) {
            String[] words = text.split(" ");
            int lineNumber = 0;
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

            int tooltipWidth = 0;
            boolean makingTooltip = false;
            String toolTipWord = "";
            int tooltipStartX = 0;
            int tooltipStartY = 0;

            for (String word : words) {
                int widthWord = fontRenderer.getStringWidth(word + " ");
                if (word.equals("/n/")) {
                    lineNumber++;
                    widthLine = 0;
                    word = "";
                } else if (word.contains("<color:")) {
                    color = Integer.decode(word.replace("<color:", "").replace(">", ""));
                    word = "";
                } else if (word.equals("</color>")) {
                    color = unchangedColor;
                    word = "";
                } else if (word.contains("<tooltip:")) {
                    toolTipWord = word.replace("<tooltip:", "").replace(">", "");
                    makingTooltip = true;
                    word = "";
                    tooltipStartX = xStart + widthLine;
                    tooltipStartY = yStart + lineNumber * 10;
                } else if (word.equals("</tooltip>")) {
                    makingTooltip = false;
                    word = "";
                } else if (widthLine + widthWord <= GuiManualBase.WIDTH - unchangedXStart) {
                    widthLine += widthWord;
                    if (makingTooltip) {
                        tooltipWidth += widthWord;
                    }
                } else {
                    lineNumber++;
                    widthLine = widthWord;
                }
                fontRenderer.drawString(word, xStart + widthLine - widthWord, yStart + lineNumber * 10, color);
                if (!makingTooltip && tooltipWidth > 0 && mouseX >= tooltipStartX && mouseX <= tooltipStartX + tooltipWidth && mouseY >= tooltipStartY && mouseY <= tooltipStartY + 9) {
                    ArrayList<String> toolTipText = new ArrayList<>();
                    toolTipText.add(toolTipWord);
                    ManualWidgetsBase.renderTooltip(mouseX, mouseY, toolTipText, 0xffffff, 0xf0100010);
                    tooltipWidth = 0;
                    toolTipWord = "";
                    tooltipStartX = 0;
                    tooltipStartY = 0;
                }
                GL11.glColor4f(1F, 1F, 1F, 1F);
            }
        }
    }



}