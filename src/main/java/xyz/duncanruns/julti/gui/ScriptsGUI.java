package xyz.duncanruns.julti.gui;

import xyz.duncanruns.julti.Julti;
import xyz.duncanruns.julti.script.Script;
import xyz.duncanruns.julti.script.ScriptManager;
import xyz.duncanruns.julti.util.GUIUtil;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ScriptsGUI extends JFrame {
    private final Julti julti;
    private boolean closed = false;
    private JPanel panel;

    public ScriptsGUI(Julti julti, JultiGUI gui) {
        this.julti = julti;
        this.setLocation(gui.getLocation());
        this.setupWindow();
        this.reload();
    }

    private void setupWindow() {
        this.setLayout(null);
        this.setTitle("Julti Scripts");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ScriptsGUI.this.onClose();
            }
        });
        this.setSize(490, 500);
        this.setVisible(true);
        this.panel = new JPanel();
        this.panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(this.panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        this.setContentPane(scrollPane);
    }

    private void reload() {
        JScrollBar verticalScrollBar = ((JScrollPane) this.getContentPane()).getVerticalScrollBar();
        int i = verticalScrollBar.getValue();
        this.panel.removeAll();

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        buttonsPanel.add(GUIUtil.getButtonWithMethod(new JButton("Import Script"), a -> this.startImportScriptDialog()));
        buttonsPanel.add(GUIUtil.createSpacer(0));
        buttonsPanel.add(GUIUtil.getButtonWithMethod(new JButton("Cancel Running Script"), a -> ScriptManager.requestCancel()));

        this.panel.add(GUIUtil.leftJustify(buttonsPanel));

        this.panel.add(GUIUtil.createSpacer(15));

        for (String name : ScriptManager.getScriptNames()) {
            this.panel.add(GUIUtil.leftJustify(new ScriptPanel(this.julti, name, ScriptManager.getHotkeyContext(name), this::reload)));
        }

        verticalScrollBar.setValue(i);
        this.revalidate();
        this.repaint();
    }

    private void onClose() {
        this.closed = true;
    }

    private void startImportScriptDialog() {
        String ans = JOptionPane.showInputDialog(this, "Enter the script string here:", "Julti: Import Script", JOptionPane.QUESTION_MESSAGE);
        if (ans == null) { return; }
        ans = ans.replace("\n", ";");

        if (ScriptManager.addScript(ans)) {
            this.reload();
            return;
        }

        if (Script.isSavableString(ans)) {
            if (ScriptManager.isDuplicateImport(ans)) {
                int replaceAns = JOptionPane.showConfirmDialog(this, "A script by the same name already exists, replace it?", "Julti: Import Script", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (replaceAns != 0) { return; }
                ScriptManager.forceAddScript(ans);
            } else {
                JOptionPane.showMessageDialog(this, "Could not import script. An unknown error occurred.", "Julti: Import Script Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Could not import script. The entered string was not a script string.", "Julti: Import Script Error", JOptionPane.ERROR_MESSAGE);
        }
        this.reload();
    }

    public boolean isClosed() {
        return this.closed;
    }
}
