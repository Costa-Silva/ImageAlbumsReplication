
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
    private final static QName _GetFileInfoResponse_QNAME = new QName("http://server.tp1.sd/", "getFileInfoResponse");
    private final static QName _InfoNotFoundException_QNAME = new QName("http://server.tp1.sd/", "InfoNotFoundException");
    private final static QName _GetFileInfo_QNAME = new QName("http://server.tp1.sd/", "getFileInfo");
    private final static QName _GetPicturesListResponse_QNAME = new QName("http://server.tp1.sd/", "getPicturesListResponse");
    private final static QName _GetAlbumList_QNAME = new QName("http://server.tp1.sd/", "getAlbumList");
    private final static QName _GetPicturesList_QNAME = new QName("http://server.tp1.sd/", "getPicturesList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: sd.tp1.client.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAlbumListResponse }
     * 
     */
    public GetAlbumListResponse createGetAlbumListResponse() {
        return new GetAlbumListResponse();
    }

    /**
     * Create an instance of {@link GetFileInfoResponse }
     * 
     */
    public GetFileInfoResponse createGetFileInfoResponse() {
        return new GetFileInfoResponse();
    }

    /**
     * Create an instance of {@link InfoNotFoundException }
     * 
     */
    public InfoNotFoundException createInfoNotFoundException() {
        return new InfoNotFoundException();
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
     * Create an instance of {@link GetFileInfo }
     * 
     */
    public GetFileInfo createGetFileInfo() {
        return new GetFileInfo();
    }

    /**
     * Create an instance of {@link GetAlbumList }
     * 
     */
    public GetAlbumList createGetAlbumList() {
        return new GetAlbumList();
    }

    /**
     * Create an instance of {@link FileInfo }
     * 
     */
    public FileInfo createFileInfo() {
        return new FileInfo();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFileInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getFileInfoResponse")
    public JAXBElement<GetFileInfoResponse> createGetFileInfoResponse(GetFileInfoResponse value) {
        return new JAXBElement<GetFileInfoResponse>(_GetFileInfoResponse_QNAME, GetFileInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InfoNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "InfoNotFoundException")
    public JAXBElement<InfoNotFoundException> createInfoNotFoundException(InfoNotFoundException value) {
        return new JAXBElement<InfoNotFoundException>(_InfoNotFoundException_QNAME, InfoNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFileInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getFileInfo")
    public JAXBElement<GetFileInfo> createGetFileInfo(GetFileInfo value) {
        return new JAXBElement<GetFileInfo>(_GetFileInfo_QNAME, GetFileInfo.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPicturesList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.tp1.sd/", name = "getPicturesList")
    public JAXBElement<GetPicturesList> createGetPicturesList(GetPicturesList value) {
        return new JAXBElement<GetPicturesList>(_GetPicturesList_QNAME, GetPicturesList.class, null, value);
    }

}
