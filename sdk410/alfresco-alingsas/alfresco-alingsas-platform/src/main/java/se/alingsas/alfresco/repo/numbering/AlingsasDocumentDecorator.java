package se.alingsas.alfresco.repo.numbering;

import org.alfresco.service.cmr.repository.NodeRef;
import org.redpill.alfresco.numbering.decorator.CurrentDatePrefixDecorator;
import org.redpill.alfresco.numbering.decorator.Decorator;

/**
 * Alingsås Kommun document number decorator.
 *
 * @author Marcus Svartmark
 */
public class AlingsasDocumentDecorator extends CurrentDatePrefixDecorator implements Decorator {

  protected String numberSeparator;
  protected int akZeroPadding;

  public void setNumberSeparator(String numberSeparator) {
    this.numberSeparator = numberSeparator;
  }

  @Override
  public void setZeroPadding(int zeroPadding) {
    //We have intentionally overridden the default setZeroPadding because we want to add our own padding at a differnt point in time
    this.akZeroPadding = zeroPadding;
  }

  protected String getPaddedAndSeparatedNumber(String number) {
    number = leftPadNumber(number, akZeroPadding, "0");
    //Format pattern
    //000-000
    //0000-000
    //0000-0000
    //Always split in the middle
    int len = number.length();
    int middle = len / 2;
    String first = number.substring(0, middle);
    String second = number.substring(middle);
    StringBuilder sb = new StringBuilder();
    sb.append(first);
    sb.append(numberSeparator);
    sb.append(second);
    return sb.toString();
  }

  @Override
  public String decorate(String number, NodeRef nodeRef) {
    return super.decorate(getPaddedAndSeparatedNumber(number), nodeRef);
  }

}
