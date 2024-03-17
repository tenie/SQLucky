package net.tenie.Sqlucky.sdk.component.editor;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.InputMethodRequests;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;

import java.util.Optional;

public
class InputMethodRequestsObject implements InputMethodRequests {
    private static Logger logger = LogManager.getLogger(InputMethodRequestsObject.class);
    private CodeArea area;

    public InputMethodRequestsObject(CodeArea area) {
        this.area = area;
    }

    @Override
    public String getSelectedText() {
        return "";
    }

    @Override
    public int getLocationOffset(int x, int y) {
        return 0;
    }

    @Override
    public void cancelLatestCommittedText() {

    }

    @Override
    public Point2D getTextLocation(int offset) {
        logger.info("输入法软件展示");
        // a very rough example, only tested under macOS
        Optional<Bounds> caretPositionBounds = area.getCaretBounds();
        if (caretPositionBounds.isPresent()) {
            Bounds bounds = caretPositionBounds.get();
            return new Point2D(bounds.getMaxX() - 5, bounds.getMaxY());
        }
        throw new NullPointerException();
    }

}