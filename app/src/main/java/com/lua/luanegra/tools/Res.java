package com.lua.luanegra.tools;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;

public class Res extends Resources {

    private int color;
    private String ToolbarColor = " ", MenuSelector = " ", TextColor = " ",AccentTextColor = " ",ButtonOutlineColor = " ", ButtonTextColor = " "
            , colorRed = " ", ColorBlack = " ", colorGold = " ", colorChatDarker = " ", colorChatLighter = " ", colorBackground = " ", colorAccent = " "
            , colorAccentDark = " ", colorPrimary = " ", colorPrimaryDark = " ", DividerColor = " ", colorRedAdmin = " ";

    public Res(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
    }



    @Override public int getColor(int id, Theme theme) throws NotFoundException {
        Log.i("Luanegra_res.java", getResourceEntryName(id));
        switch (getResourceEntryName(id)) {

            case "colorRedAdmin":
                if(!colorRedAdmin.equals(" ")){
                    color = Color.parseColor(colorRedAdmin);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "DividerColor":
                if(!DividerColor.equals(" ")){
                    color = Color.parseColor(DividerColor);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorPrimaryDark":
                if(!colorPrimaryDark.equals(" ")){
                    color = Color.parseColor(colorPrimaryDark);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorPrimary":
                if(!colorPrimary.equals(" ")){
                    color = Color.parseColor(colorPrimary);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorAccentDark":
                if(!colorAccentDark.equals(" ")){
                    color = Color.parseColor(colorAccentDark);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorAccent":
                if(!colorAccent.equals(" ")){
                    color = Color.parseColor(colorAccent);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorBackground":
                if(!colorBackground.equals(" ")){
                    color = Color.parseColor(colorBackground);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorChatLighter":
                if(!colorChatLighter.equals(" ")){
                    color = Color.parseColor(colorChatLighter);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorChatDarker":
                if(!colorChatDarker.equals(" ")){
                    color = Color.parseColor(colorChatDarker);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorGold":
                if(!colorGold.equals(" ")){
                    color = Color.parseColor(colorGold);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "ColorBlack":
                if(!ColorBlack.equals(" ")){
                    color = Color.parseColor(ColorBlack);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "colorRed":
                if(!colorRed.equals(" ")){
                    color = Color.parseColor(colorRed);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "ButtonTextColor":
                if(!ButtonTextColor.equals(" ")){
                    color = Color.parseColor(ButtonTextColor);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "ButtonOutlineColor":
                if(!ButtonOutlineColor.equals(" ")){
                    color = Color.parseColor(ButtonOutlineColor);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "ToolbarColor":
                if(!ToolbarColor.equals(" ")){
                    color = Color.parseColor(ToolbarColor);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            case "MenuSelector":
                if(!MenuSelector.equals(" ")){
                    color = Color.parseColor(MenuSelector);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }

            case "TextColor":
                if(!TextColor.equals(" ")){
                    color = Color.parseColor(TextColor);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }

            case "AccentTextColor":
                if(AccentTextColor != " "){
                    color = Color.parseColor(AccentTextColor);
                    return color;
                }else {
                    return super.getColor(id, theme);
                }
            default:
                return super.getColor(id, theme);
        }
    }

    public String getAccentTextColor() {
        return AccentTextColor;
    }

    public int getColor() {
        return color;
    }

    public String getButtonOutlineColor() {
        return ButtonOutlineColor;
    }

    public String getButtonTextColor() {
        return ButtonTextColor;
    }

    public String getColorAccent() {
        return colorAccent;
    }

    public String getColorAccentDark() {
        return colorAccentDark;
    }

    public String getColorBackground() {
        return colorBackground;
    }

    public String getColorBlack() {
        return ColorBlack;
    }

    public String getColorChatDarker() {
        return colorChatDarker;
    }

    public String getColorGold() {
        return colorGold;
    }

    public String getColorChatLighter() {
        return colorChatLighter;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public String getColorPrimaryDark() {
        return colorPrimaryDark;
    }

    public String getColorRed() {
        return colorRed;
    }

    public String getMenuSelector() {
        return MenuSelector;
    }

    public String getTextColor() {
        return TextColor;
    }

    public String getToolbarColor() {
        return ToolbarColor;
    }

    public String getColorRedAdmin() {
        return colorRedAdmin;
    }

    public String getDividerColor() {
        return DividerColor;
    }

    public void setAccentTextColor(String accentTextColor) {
        AccentTextColor = accentTextColor;
    }

    public void setButtonOutlineColor(String buttonOutlineColor) {
        ButtonOutlineColor = buttonOutlineColor;
    }

    public void setButtonTextColor(String buttonTextColor) {
        ButtonTextColor = buttonTextColor;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColorAccent(String colorAccent) {
        this.colorAccent = colorAccent;
    }

    public void setColorAccentDark(String colorAccentDark) {
        this.colorAccentDark = colorAccentDark;
    }

    public void setColorBackground(String colorBackground) {
        this.colorBackground = colorBackground;
    }

    public void setColorBlack(String colorBlack) {
        ColorBlack = colorBlack;
    }

    public void setColorChatDarker(String colorChatDarker) {
        this.colorChatDarker = colorChatDarker;
    }

    public void setColorChatLighter(String colorChatLighter) {
        this.colorChatLighter = colorChatLighter;
    }

    public void setColorGold(String colorGold) {
        this.colorGold = colorGold;
    }

    public void setColorPrimary(String colorPrimary) {
        this.colorPrimary = colorPrimary;
    }

    public void setColorPrimaryDark(String colorPrimaryDark) {
        this.colorPrimaryDark = colorPrimaryDark;
    }

    public void setColorRed(String colorRed) {
        this.colorRed = colorRed;
    }

    public void setColorRedAdmin(String colorRedAdmin) {
        this.colorRedAdmin = colorRedAdmin;
    }

    public void setDividerColor(String dividerColor) {
        DividerColor = dividerColor;
    }

    public void setMenuSelector(String menuSelector) {
        MenuSelector = menuSelector;
    }

    public void setTextColor(String textColor) {
        TextColor = textColor;
    }

    public void setToolbarColor(String toolbarColor) {
        ToolbarColor = toolbarColor;
    }

}