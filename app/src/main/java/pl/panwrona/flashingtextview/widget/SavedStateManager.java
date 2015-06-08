package pl.panwrona.flashingtextview.widget;

class SavedStateManager {

    private boolean isFlashing;

    public SavedStateManager(FlashingTextView flashingTextView) {
        this.isFlashing = flashingTextView.isFlashing();
    }

}
