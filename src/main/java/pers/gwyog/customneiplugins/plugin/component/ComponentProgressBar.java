package pers.gwyog.customneiplugins.plugin.component;

public class ComponentProgressBar {
    public int posX;
    public int posY;
    public int textureX;
    public int textureY;
    public int width;
    public int height;
    public int ticks;
    public int direction;
    
    public ComponentProgressBar(int posX, int posY, int textureX, int textureY, int width, int height, int ticks, int direction) {
        this.posX = posX;
        this.posY = posY;
        this.textureX = textureX;
        this.textureY = textureY;
        this.width = width;
        this.height = height;
        this.ticks = ticks;
        this.direction = direction;
    }
    
}
