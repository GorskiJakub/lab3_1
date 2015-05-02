package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.mockito.Mockito;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperTest {

	public BookKeeper bookKeeper;

	@Test
	public void testCase1_IfRequestedFactureWithOnePostition_ShouldReturnOnePosition() {

		Money money = new Money(20);
		Id id = new Id("1");

		ClientData clientData = new ClientData(id, "klient");

		BookKeeper book;

		TaxPolicy taxPolicy = mock(TaxPolicy.class);

		InvoiceFactory InvoiceFactoryMock = mock(InvoiceFactory.class);
		when(InvoiceFactoryMock.create(clientData)).thenReturn(
				new Invoice(id, clientData));
		book = new BookKeeper(InvoiceFactoryMock);

		InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

		when(taxPolicy.calculateTax(ProductType.FOOD, money)).thenReturn(
				new Tax(money, "spis"));

		ProductData productData = new ProductDataBuilder().withPrice(20)
				.withProductType(ProductType.FOOD).build();

		new RequestItemBuilder().withProductData(productData).witTotalCost(4)
				.build();

		Invoice invoiceResult = book.issuance(invoiceRequest, taxPolicy);
		int result = invoiceResult.getItems().size();

		assertThat(result, is(1));

	}

	@Test
	public void testcase_2_IFPositionInvoiceRequested_callCalculateTaxTwoTimes() {

		Id id = new Id("1");
		Money moneyEveryItem = new Money(1);
		ProductType productTypeEveryItem = ProductType.FOOD;
		ClientData clientData = new ClientData(id, "klient");
		ProductData productData = new ProductDataBuilder().withPrice(20)
				.withProductType(ProductType.FOOD).build();
		RequestItem requestItem = new RequestItemBuilder()
				.withProductData(productData).witTotalCost(4).build();

		InvoiceFactory mockInvoiceFactory = mock(InvoiceFactory.class);
		bookKeeper = new BookKeeper(mockInvoiceFactory);
		when(mockInvoiceFactory.create(clientData)).thenReturn(
				new Invoice(id, clientData));
		TaxPolicy taxPolicy = mock(TaxPolicy.class);
		when(taxPolicy.calculateTax(productTypeEveryItem, moneyEveryItem))
				.thenReturn(new Tax(moneyEveryItem, "spis"));

		InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
		invoiceRequest.add(requestItem);

		bookKeeper.issuance(invoiceRequest, taxPolicy);

		Mockito.verify(taxPolicy, Mockito.times(2)).calculateTax(
				productTypeEveryItem, moneyEveryItem);
	}

	@Test
	public void testCase3_IfRequestedFactureWithNoPostition_ShouldReturnWithoutPosition() {

		Id id = new Id("1");
		Money money = new Money(1);
		InvoiceFactory mockInvoiceFactory = mock(InvoiceFactory.class);
		bookKeeper = new BookKeeper(mockInvoiceFactory);
		ClientData clientData = new ClientData(id, "klient");
		when(mockInvoiceFactory.create(clientData)).thenReturn(
				new Invoice(id, clientData));
		InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
		TaxPolicy taxPolicy = mock(TaxPolicy.class);
		when(taxPolicy.calculateTax(ProductType.FOOD, money)).thenReturn(
				new Tax(money, "opis"));
		new ProductDataBuilder().withPrice(20)
				.withProductType(ProductType.FOOD).build();

		Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);
		int result = invoiceResult.getItems().size();

		assertThat(result, is(0));
	}

	@Test
	public void testCase4_IfNoPositionInvoiceRequested_notCallCalculateTax() {

		Id id = new Id("1");
		Money moneyEveryItem = new Money(1);
		ProductType productTypeEveryItem = ProductType.FOOD;
		ClientData clientData = new ClientData(id, "klient");
		ProductData productData = new ProductDataBuilder().withPrice(20)
				.withProductType(ProductType.FOOD).build();
		new RequestItemBuilder().withProductData(productData).witTotalCost(4)
				.build();

		InvoiceFactory mockInvoiceFactory = mock(InvoiceFactory.class);
		bookKeeper = new BookKeeper(mockInvoiceFactory);
		when(mockInvoiceFactory.create(clientData)).thenReturn(
				new Invoice(id, clientData));
		TaxPolicy taxPolicy = mock(TaxPolicy.class);
		when(taxPolicy.calculateTax(productTypeEveryItem, moneyEveryItem))
				.thenReturn(new Tax(moneyEveryItem, "spis"));

		InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

		bookKeeper.issuance(invoiceRequest, taxPolicy);

		Mockito.verify(taxPolicy, Mockito.times(0)).calculateTax(
				productTypeEveryItem, moneyEveryItem);
	}
}