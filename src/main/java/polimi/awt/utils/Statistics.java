package polimi.awt.utils;

import java.math.BigDecimal;

//class in order to display statistics results in page
public class Statistics {

    private BigDecimal totalPeaks = new BigDecimal(0);
    private BigDecimal peaksStarted= new BigDecimal(0);
    private BigDecimal peaksStartedPercentage= new BigDecimal(0);
    private BigDecimal peaksAnnotated= new BigDecimal(0);
    private BigDecimal peaksAnnotatedPercentage= new BigDecimal(0);
    private BigDecimal peaksWithRejectedAnnotation= new BigDecimal(0); //red
    private BigDecimal peaksWithRejectedAnnotationPercentage= new BigDecimal(0);
    private BigDecimal conflicts= new BigDecimal(0);

    public BigDecimal getTotalPeaks() {
        return totalPeaks;
    }

    public void setTotalPeaks(BigDecimal totalPeaks) {
        this.totalPeaks = totalPeaks;
    }

    public BigDecimal getPeaksStarted() {
        return peaksStarted;
    }

    public void setPeaksStarted(BigDecimal peaksStarted) {
        this.peaksStarted = peaksStarted;
    }

    public BigDecimal getPeaksAnnotated() {
        return peaksAnnotated;
    }

    public void setPeaksAnnotated(BigDecimal peaksAnnotated) {
        this.peaksAnnotated = peaksAnnotated;
    }

    public BigDecimal getPeaksWithRejectedAnnotation() {
        return peaksWithRejectedAnnotation;
    }

    public void setPeaksWithRejectedAnnotation(BigDecimal peaksWithRejectedAnnotation) {
        this.peaksWithRejectedAnnotation = peaksWithRejectedAnnotation;
    }

    public BigDecimal getConflicts() {
        return conflicts;
    }

    public void setConflicts(BigDecimal conflicts) {
        this.conflicts = conflicts;
    }

    public BigDecimal getPeaksStartedPercentage() {
        return peaksStartedPercentage;
    }

    public void setPeaksStartedPercentage(BigDecimal peaksStartedPercentage) {
        this.peaksStartedPercentage = peaksStartedPercentage;
    }

    public BigDecimal getPeaksAnnotatedPercentage() {
        return peaksAnnotatedPercentage;
    }

    public void setPeaksAnnotatedPercentage(BigDecimal peaksAnnotatedPercentage) {
        this.peaksAnnotatedPercentage = peaksAnnotatedPercentage;
    }

    public BigDecimal getPeaksWithRejectedAnnotationPercentage() {
        return peaksWithRejectedAnnotationPercentage;
    }

    public void setPeaksWithRejectedAnnotationPercentage(BigDecimal peaksWithRejectedAnnotationPercentage) {
        this.peaksWithRejectedAnnotationPercentage = peaksWithRejectedAnnotationPercentage;
    }
}
