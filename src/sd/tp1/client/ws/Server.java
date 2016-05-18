
package sd.tp1.client.ws;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Server", targetNamespace = "http://server.tp1.sd/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Server {


    /**
     * 
     * @return
     *     returns byte[]
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMetaData", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetMetaData")
    @ResponseWrapper(localName = "getMetaDataResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetMetaDataResponse")
    @Action(input = "http://server.tp1.sd/Server/getMetaDataRequest", output = "http://server.tp1.sd/Server/getMetaDataResponse")
    public byte[] getMetaData();

    /**
     * 
     * @return
     *     returns long
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getserverSpace", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetserverSpace")
    @ResponseWrapper(localName = "getserverSpaceResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetserverSpaceResponse")
    @Action(input = "http://server.tp1.sd/Server/getserverSpaceRequest", output = "http://server.tp1.sd/Server/getserverSpaceResponse")
    public long getserverSpace();

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createAlbum", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.CreateAlbum")
    @ResponseWrapper(localName = "createAlbumResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.CreateAlbumResponse")
    @Action(input = "http://server.tp1.sd/Server/createAlbumRequest", output = "http://server.tp1.sd/Server/createAlbumResponse")
    public String createAlbum(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "deletePicture", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.DeletePicture")
    @ResponseWrapper(localName = "deletePictureResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.DeletePictureResponse")
    @Action(input = "http://server.tp1.sd/Server/deletePictureRequest", output = "http://server.tp1.sd/Server/deletePictureResponse")
    public boolean deletePicture(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1);

    /**
     * 
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "deleteAlbum", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.DeleteAlbum")
    @ResponseWrapper(localName = "deleteAlbumResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.DeleteAlbumResponse")
    @Action(input = "http://server.tp1.sd/Server/deleteAlbumRequest", output = "http://server.tp1.sd/Server/deleteAlbumResponse")
    public boolean deleteAlbum(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAlbumList", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetAlbumList")
    @ResponseWrapper(localName = "getAlbumListResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetAlbumListResponse")
    @Action(input = "http://server.tp1.sd/Server/getAlbumListRequest", output = "http://server.tp1.sd/Server/getAlbumListResponse")
    public List<String> getAlbumList();

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns byte[]
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPictureData", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetPictureData")
    @ResponseWrapper(localName = "getPictureDataResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetPictureDataResponse")
    @Action(input = "http://server.tp1.sd/Server/getPictureDataRequest", output = "http://server.tp1.sd/Server/getPictureDataResponse")
    public byte[] getPictureData(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPicturesList", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetPicturesList")
    @ResponseWrapper(localName = "getPicturesListResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.GetPicturesListResponse")
    @Action(input = "http://server.tp1.sd/Server/getPicturesListRequest", output = "http://server.tp1.sd/Server/getPicturesListResponse")
    public List<String> getPicturesList(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "uploadPicture", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.UploadPicture")
    @ResponseWrapper(localName = "uploadPictureResponse", targetNamespace = "http://server.tp1.sd/", className = "sd.tp1.client.ws.UploadPictureResponse")
    @Action(input = "http://server.tp1.sd/Server/uploadPictureRequest", output = "http://server.tp1.sd/Server/uploadPictureResponse")
    public boolean uploadPicture(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        byte[] arg2);

}
