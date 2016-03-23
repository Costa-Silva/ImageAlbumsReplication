
package sd.tp1.client.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the sd.tp1.client.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetAlbumListResponse_QNAME = new QName("http://server.tp1.sd/", "getAlbumListResponse");
    private final static QName _GetPictureData_QNAME = new QName("http://server.tp1.sd/", "getPictureData");
    private final static QName _GetPictureDataResponse_QNAME = new QName("http://server.tp1.sd/", "getPictureDataResponse");
    private final static QName _UpdatePicture_QNAME = new QName("http://server.tp1.sd/", "updatePicture");
    private final static QName _DeleteAlbum_QNAME = new QName("http://server.tp1.sd/", "deleteAlbum");
    private final static QName _DeleteAlbumResponse_QNAME = new QName("http://server.tp1.sd/", "deleteAlbumResponse");
    private final static QName _CreateAlbumResponse_QNAME = new QName("http://server.tp1.sd/", "createAlbumResponse");
    private final static QName _GetPicturesListResponse_QNAME = new QName("http://server.tp1.sd/", "getPicturesListResponse");
    private final static QName _GetAlbumList_QNAME = new QName("http://server.tp1.sd/", "getAlbumList");
    private final static QName _CreateAlbum_QNAME = new QName("http://server.tp1.sd/", "createAlbum");
    private final static QName _UpdatePictureResponse_QNAME = new QName("http://server.tp1.sd/", "updatePictureResponse");
    private final static QName _GetPicturesList_QNAME = new QName("http://server.tp1.sd/", "getPicturesList");
    private final static QName _GetPictureDataResponseReturn_QNAME = new QName("", "return");
    private final static QName _UpdatePictureArg2_QNAME = new QName("", "arg2");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: sd.tp1.client.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdatePicture }
     * 
     */
    public UpdatePicture createUpdatePicture() {
        return new UpdatePicture();
    }

    /**
     * Create an instance of {@link DeleteAlbumResponse }
     * 
     */
    public DeleteAlbumResponse createDeleteAlbumResponse() {
        return new DeleteAlbumResponse();
    }

    /**
     * Create an instance of {@link DeleteAlbum }
     * 
     */
    public DeleteAlbum createDeleteAlbum() {
        return new DeleteAlbum();
    }

    /**
     * Create an instance of {@link GetAlbumListResponse }
     * 
     */
    public GetAlbumListResponse createGetAlbumListResponse() {
        return new GetAlbumListResponse();
    }

    /**
     * Create an instance of {@link GetPictureData }
     * 
     */
    public GetPictureData createGetPictureData() {
        return new GetPictureData();
    }

    /**
     * Create an instance of {@link GetPictureDataResponse }
     * 
     */
    public GetPictureDataResponse createGetPictureDataResponse() {
        return new GetPictureDataResponse();
    }

    /**
     * Create an instance of {@link UpdatePictureResponse }
     * 
     */
    public UpdatePictureResponse createUpdatePictureResponse() {
        return new UpdatePictureResponse();
    }

    /**
     * Create an instance of {@link CreateAlbum }
     * 
     */
    public CreateAlbum createCreateAlbum() {
        return new CreateAlbum();
    }

    /**
     * Create an instance of {@link GetPicturesList }
     * 
     */
    public GetPicturesList createGetPicturesList() {
        return new GetPicturesList();
    }

    /**
     * Create an instance of {@link GetPicturesListResponse }
     * 
     */
    public GetPicturesListResponse createGetPicturesListResponse() {
        return new GetPicturesListResponse();
    }

    /**
     * Create an instance of {@link CreateAlbumResponse }
     * 
     */
    public CreateAlbumResponse createCreateAlbumResponse() {
        return new CreateAlbumResponse();
    }

    /**
     * Create an instance of {@link GetAlbumList }
     * 
     */
    public GetAlbumList createGetAlbumList() {
        return new GetAlbumList();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAlbumListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getAlbumListResponse")
    public JAXBElement<GetAlbumListResponse> createGetAlbumListResponse(GetAlbumListResponse value) {
        return new JAXBElement<GetAlbumListResponse>(_GetAlbumListResponse_QNAME, GetAlbumListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPictureData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getPictureData")
    public JAXBElement<GetPictureData> createGetPictureData(GetPictureData value) {
        return new JAXBElement<GetPictureData>(_GetPictureData_QNAME, GetPictureData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPictureDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getPictureDataResponse")
    public JAXBElement<GetPictureDataResponse> createGetPictureDataResponse(GetPictureDataResponse value) {
        return new JAXBElement<GetPictureDataResponse>(_GetPictureDataResponse_QNAME, GetPictureDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdatePicture }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "updatePicture")
    public JAXBElement<UpdatePicture> createUpdatePicture(UpdatePicture value) {
        return new JAXBElement<UpdatePicture>(_UpdatePicture_QNAME, UpdatePicture.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAlbum }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "deleteAlbum")
    public JAXBElement<DeleteAlbum> createDeleteAlbum(DeleteAlbum value) {
        return new JAXBElement<DeleteAlbum>(_DeleteAlbum_QNAME, DeleteAlbum.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAlbumResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "deleteAlbumResponse")
    public JAXBElement<DeleteAlbumResponse> createDeleteAlbumResponse(DeleteAlbumResponse value) {
        return new JAXBElement<DeleteAlbumResponse>(_DeleteAlbumResponse_QNAME, DeleteAlbumResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateAlbumResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "createAlbumResponse")
    public JAXBElement<CreateAlbumResponse> createCreateAlbumResponse(CreateAlbumResponse value) {
        return new JAXBElement<CreateAlbumResponse>(_CreateAlbumResponse_QNAME, CreateAlbumResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPicturesListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getPicturesListResponse")
    public JAXBElement<GetPicturesListResponse> createGetPicturesListResponse(GetPicturesListResponse value) {
        return new JAXBElement<GetPicturesListResponse>(_GetPicturesListResponse_QNAME, GetPicturesListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAlbumList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getAlbumList")
    public JAXBElement<GetAlbumList> createGetAlbumList(GetAlbumList value) {
        return new JAXBElement<GetAlbumList>(_GetAlbumList_QNAME, GetAlbumList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateAlbum }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "createAlbum")
    public JAXBElement<CreateAlbum> createCreateAlbum(CreateAlbum value) {
        return new JAXBElement<CreateAlbum>(_CreateAlbum_QNAME, CreateAlbum.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdatePictureResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "updatePictureResponse")
    public JAXBElement<UpdatePictureResponse> createUpdatePictureResponse(UpdatePictureResponse value) {
        return new JAXBElement<UpdatePictureResponse>(_UpdatePictureResponse_QNAME, UpdatePictureResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPicturesList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getPicturesList")
    public JAXBElement<GetPicturesList> createGetPicturesList(GetPicturesList value) {
        return new JAXBElement<GetPicturesList>(_GetPicturesList_QNAME, GetPicturesList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = GetPictureDataResponse.class)
    public JAXBElement<byte[]> createGetPictureDataResponseReturn(byte[] value) {
        return new JAXBElement<byte[]>(_GetPictureDataResponseReturn_QNAME, byte[].class, GetPictureDataResponse.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg2", scope = UpdatePicture.class)
    public JAXBElement<byte[]> createUpdatePictureArg2(byte[] value) {
        return new JAXBElement<byte[]>(_UpdatePictureArg2_QNAME, byte[].class, UpdatePicture.class, ((byte[]) value));
    }

}
