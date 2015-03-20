package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Test;

import pl.com.bottega.cqrs.command.handler.*;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;


public class BookKeeperTest {
	
	private BookKeeper bookKeeper;


	@Test
	public void requestOneInvoiceShouldReturnOneInvoiceWithOnePosition() {
		Money money = new Money(3);
		Id id = new Id("1");
		ClientData clientData = new ClientData(new Id("1"), "Faktura 1");
		when(invoiceFactory.create(clientData)).thenReturn(new Id("1", clientData));
		
		
		InvoiceRequest invoiceRequest = new InvoiceRequest(new ClientData(new Id("1"), "Faktura1"));

		ProductData productData = new ProductData(new Id("1"), new Money(1), "ksiazka o czyms", new Date(0), ProductType.DRUG);
		RequestItem requestItem = new RequestItem(productData, 2 ,new Money(2));
		
		TaxPolicy taxPolicy = mock(TaxPolicy.class);
		when(taxPolicy.calculateTax(ProductType.DRUG, money)).thenReturn(new Tax(money, "spis"));
		invoiceRequest.add(requestItem);
		invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
	}
	
	InvoiceFactory invoiceFactory = mock(InvoiceFactory.class);
	
	when(invoiceFactory.create(clientData)).thenReturn(new Id("1", clientData));
	bookKeeper = new BookKeeper(new InvoiceFactory());

}
