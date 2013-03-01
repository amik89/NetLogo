package org.nlogo.deltatick;

import org.nlogo.deltatick.dialogs.ColorButton;
import org.nlogo.deltatick.dnd.PrettyInput;
import org.nlogo.deltatick.dnd.VariationDropDown;
import org.nlogo.deltatick.xml.Trait;
import org.nlogo.deltatick.xml.Variation;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
//import javax.swing.


/**
 * Created by IntelliJ IDEA.
 * User: aditiwagh
 * Date: 2/4/12
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */

// this block will hold behaviors for agents with this variation
// not sure if this should be abstract -Feb 4
public strictfp class TraitBlock
        extends CodeBlock
{
    JTextField textName;
    ArrayList<String> varList;
    ArrayList<String> numList;
    LinkedList<Variation> variationList = new LinkedList<Variation>();
    String breedName;
    String traitName;
    JLabel name = new JLabel();

    transient Trait trait;
    transient Frame parentFrame;
    Variation variation;
    JList TraitsList;
    HashMap<String, String> varPercentage;
    HashMap<String, String> traitNumVar = new HashMap<String, String>();
    HashMap<String, Integer> varNum = new HashMap<String, Integer>();
    PrettyInput number;
    String color;
    ColorButton colorButton = new ColorButton(parentFrame, this);
    VariationDropDown dropdownList;

    JPanel rectPanel;
    Boolean removedRectPanel = false;

    //variables for second constructor
    HashMap<String, Variation> variationHashMap = new HashMap<String, Variation>();
    HashMap<String, String> variationNamesValues = new HashMap<String, String>();
    HashMap<String, String> variationNumbers = new HashMap<String, String>();
    HashMap<String, String> valueNumbers = new HashMap<String, String>();
    HashMap<String, String> varColorName = new HashMap<String, String>();
    HashMap<String, Color> varColor = new HashMap<String, Color>();


    // this constructor is called when traits are selected from the library
    public TraitBlock (BreedBlock breedBlock, Trait trait, HashMap<String, Variation> variationHashMap, HashMap<String, String> variationValues) {
        super(trait.getNameTrait(), Color.lightGray);
        flavors = new DataFlavor[]{
                DataFlavor.stringFlavor,
                traitBlockFlavor,
                CodeBlock.codeBlockFlavor};
        this.breedName = breedBlock.plural();
        this.trait = trait;
        this.traitName = trait.getNameTrait();
        this.variationHashMap = variationHashMap;
        this.variationNamesValues = variationValues;

        varColor = new HashMap<String, Color>();
        for (Map.Entry<String, Variation> variationEntry: variationHashMap.entrySet()) {
            String variation = variationEntry.getKey();
            varColor.put(variation, Color.lightGray);
        }

        dropdownList = new VariationDropDown(trait.getVariationsList(), this);
        dropdownList.setEnabled(false);
        java.util.List<Component> componentList = new ArrayList<Component>(5);
        componentList.add(name); componentList.add(dropdownList);
        //componentList.add(number);
        int y = 0;

        for (Component c : componentList) {
          label.add(c);
          y += c.getPreferredSize().getHeight();
        }

        //label.add(colorButton);
        label.setPreferredSize(new Dimension(100, y + 11));
        newLabel();
        this.revalidate();
    }

    public void makeNumberActive() {
        number.getDocument().addDocumentListener(new myDocumentListener());
    }

    protected class myDocumentListener implements DocumentListener {
    public void insertUpdate(DocumentEvent e) {
      // updateNumber();
    }
    public void removeUpdate(DocumentEvent e) {
        //updateNumber();
        //System.out.println("remove");
    }
    public void changedUpdate(DocumentEvent e) {
        //displayEditInfo(e);
        System.out.println("change");
    }
    }

    public void setMyParent(CodeBlock block) {
        myParent = block;
    }

    public String getTraitName() {
        return traitName;
    }

    public String unPackAsCode() {
        if (myParent == null) {

            return unPackAsProcedure();
        }

        return unPackAsCommand();
    }

    public void numberAgents() {
        int i = 0;
        int accumulatedTotal = 0;
        int totalAgents = 0;
        int numberOfVariation;
        String tmp;

        tmp = ((BreedBlock) myParent).number.getText().toString();
        totalAgents = Integer.parseInt(tmp);


            for (Map.Entry<String, String> entry : varPercentage.entrySet()) {
                String variationType = entry.getKey();
                String numberType = entry.getValue();

                int k = Integer.parseInt(entry.getValue());

                if (i == (varPercentage.size() - 1)) {
                    numberOfVariation = (totalAgents - accumulatedTotal);
                }
                else {
                    numberOfVariation = (int) ( ( (float) k/100.0) * (float) totalAgents);
                }
                varNum.put(variationType, numberOfVariation);
                accumulatedTotal += numberOfVariation;
                i++;
            }
    }

    public void updateNumber() {
        String name = dropdownList.getSelectedItem().toString();
        Variation tmp = variationHashMap.get(name);
        tmp.number = Integer.parseInt(number.getText());
        variationHashMap.put(name, tmp);
    }

    public HashMap<String, Integer> getVarNum() {
        return varNum;
    }

    public String getMyTraitName() {
        String passback = "";
        passback += traitName + "\n ";
        return passback;
    }


    public String unPackAsCommand() {
        String passBack = "";
        String variation = dropdownList.getSelectedVariation();
        String value = variationNamesValues.get(variation);
        passBack += "if " + this.getTraitName() + " = " + value + " [\n";
        for (CodeBlock block : myBlocks) {
            passBack += block.unPackAsCode();
        }
        passBack += "] \n";
        return passBack;
    }

    public String unPackAsProcedure() {
        String passBack = "";
        return passBack;
    }

    public ArrayList<String> getVariations() {
        return varList;
    }

    public void addBlock(CodeBlock block) {
        myBlocks.add(block);
        this.add(block);
        block.enableInputs();

        block.showRemoveButton();
        this.add(Box.createRigidArea(new Dimension(this.getWidth(), 4)));
        if (removedRectPanel == false) {     //checking if rectPanel needs to be removed
            remove(rectPanel);
            removedRectPanel = true;
        }
        block.setMyParent(this);
        block.doLayout();

        block.validate();
        block.repaint();
        if (block instanceof BehaviorBlock) {
            ((BehaviorBlock) block).updateBehaviorInput();

        }
        if (block instanceof BehaviorBlock || block instanceof ConditionBlock) {
            String tmp = ((BehaviorBlock) block).getBehaviorInputName();
            addBehaviorInputToList(tmp);
            String s = ((BehaviorBlock) block).getAgentInputName();
            addAgentInputToList(s);
        }
        doLayout();
        validate();
        repaint();

        this.getParent().doLayout();
        this.getParent().validate();
        this.getParent().repaint();
    }



    public void newLabel() {
//        for (Map.Entry<String, Color> entry : varColor.entrySet()) {
//            String string = entry.getKey();
//            if (dropdownList.getSelectedItem().toString().equals(string)) {
//                setButtonColor(entry.getValue());
//            }
//        }
        //commented this out because I don't want number to be on TraitBlock - Feb 14, 2013
//        for (Map.Entry<String, Variation> entry : variationHashMap.entrySet()) {
//            String variation = entry.getKey();
//            int num = entry.getValue().number;
//            if (dropdownList.getSelectedItem().toString().equals(variation)) {
//                number.setText(Integer.toString(num));
//            }
//        }
    }

    public void addRect() {
        rectPanel = new JPanel();
        rectPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        rectPanel.setPreferredSize(new Dimension(this.getWidth(), 40));
        JLabel label = new JLabel();
        label.setText("Add blocks here");
        rectPanel.add(label);
        add(rectPanel);
    }


    public void enableDropDown() {
        dropdownList.setEnabled(true);
    }

    public String selectedColor() {
        return colorButton.getSelectedColorName();
    }

    public void setButtonColor( Color color ) {
        colorButton.setBackground(color);
        colorButton.setOpaque(true);
        colorButton.setBorderPainted(false);
    }


    public void addVarColor() {
        String name = dropdownList.getSelectedItem().toString();
        varColor.put(name, colorButton.getSelectedColor());
        Variation tmp = variationHashMap.get(name);
        tmp.color = colorButton.getSelectedColorName();
        variationHashMap.put(name, tmp);
    }


    public void showColorButton() {
        colorButton.setVisible(true);
    }

    public VariationDropDown getDropdownList() {
        return dropdownList;
    }

    public HashMap<String, Variation> getVariationHashMap() {
        return variationHashMap;
    }

    public String getActiveVariation() {
        return dropdownList.getSelectedItem().toString();
    }

    public String getBreedName() {
        return breedName;
    }

}

