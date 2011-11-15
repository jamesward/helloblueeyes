package net.interdoodle.example.httpstaticfileserver

import org.jboss.netty.handler.codec.http.HttpHeaders._
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import org.jboss.netty.handler.codec.http.HttpMethod._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion._

import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

import javax.activation.MimetypesFileTypeMap

import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelFuture
import org.jboss.netty.channel.ChannelFutureListener
import org.jboss.netty.channel.ChannelFutureProgressListener
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.DefaultFileRegion
import org.jboss.netty.channel.ExceptionEvent
import org.jboss.netty.channel.FileRegion
import org.jboss.netty.channel.MessageEvent
import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import org.jboss.netty.handler.codec.frame.TooLongFrameException
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpHeaders
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.util.CharsetUtil

class HttpStaticFileServerHandler(val dirName: String) extends SimpleChannelUpstreamHandler {

    val HTTP_DATE_FORMAT: String = "EEE, dd MMM yyyy HH:mm:ss zzz"
    val HTTP_DATE_GMT_TIMEZONE: String = "GMT"
    val HTTP_CACHE_SECONDS: Int = 60

    override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
        val request: HttpRequest = e.getMessage().asInstanceOf[HttpRequest]
        if (request.getMethod() != GET) {
            sendError(ctx, METHOD_NOT_ALLOWED)
            return
        }

        val path: String = sanitizeUri(request.getUri())
        if (path == null) {
            sendError(ctx, FORBIDDEN)
            return
        }

        val file: File = new File(path)
        if (file.isHidden() || !file.exists()) {
            sendError(ctx, NOT_FOUND)
            return
        }
        if (!file.isFile()) {
            sendError(ctx, FORBIDDEN)
            return
        }

        // Cache Validation
        val ifModifiedSince: String = request.getHeader(HttpHeaders.Names.IF_MODIFIED_SINCE)
        if (ifModifiedSince != null && !ifModifiedSince.equals(""))
        {
            val dateFormatter: SimpleDateFormat = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US)
            val ifModifiedSinceDate: Date = dateFormatter.parse(ifModifiedSince)

            // Only compare up to the second because the datetime format we send to the client does not have milliseconds 
            val ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000
            val fileLastModifiedSeconds = file.lastModified() / 1000
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                sendNotModified(ctx)
                return
            }
        }
        
        var raf: RandomAccessFile = null
      
        try {
            raf = new RandomAccessFile(file, "r")
        }
        catch {
          case fnfe: FileNotFoundException =>
            sendError(ctx, NOT_FOUND)
            return
        }

        val fileLength = raf.length()

        val response: HttpResponse = new DefaultHttpResponse(HTTP_1_1, OK)
        setContentLength(response, fileLength)
        setContentTypeHeader(response, file)
        setDateAndCacheHeaders(response, file)
        
        val ch: Channel = e.getChannel()

        // Write the initial line and the header.
        ch.write(response)

        // Write the content.
        var writeFuture: ChannelFuture = null


        val region: FileRegion = new DefaultFileRegion(raf.getChannel(), 0, fileLength)
        writeFuture = ch.write(region)
        writeFuture.addListener(new ChannelFutureProgressListener() {

            def operationComplete(future: ChannelFuture) {
                region.releaseExternalResources()
            }

            def operationProgressed(future: ChannelFuture, amount: Long, current: Long, total: Long) {
                System.out.printf("%s: %d / %d (+%d)%n", path, current.toString, total.toString, amount.toString)
            }
        })

        // Decide whether to close the connection or not.
        if (!isKeepAlive(request)) {
            // Close the connection when the whole content is written out.
            writeFuture.addListener(ChannelFutureListener.CLOSE)
        }
    }

    override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
        val ch: Channel = e.getChannel()
        val cause: Throwable = e.getCause()

        if (cause.isInstanceOf[TooLongFrameException]) {
            sendError(ctx, BAD_REQUEST)
            return
        }

        cause.printStackTrace()

        if (ch.isConnected()) {
            sendError(ctx, INTERNAL_SERVER_ERROR)
        }
    }

    def sanitizeUri(uri: String): String = {
      
      var myuri: String = uri
      
        // Decode the path.
        try {
           myuri = URLDecoder.decode(uri, "UTF-8")
        }
        catch {
          case e: UnsupportedEncodingException =>
            try {
                myuri = URLDecoder.decode(uri, "ISO-8859-1")
            }
            catch {
              case e1: UnsupportedEncodingException =>
                throw new Error()
            }
        }

        // Convert file separators.
        myuri = uri.replace('/', File.separatorChar)

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (myuri.contains(File.separator + ".") ||
            myuri.contains("." + File.separator) ||
            myuri.startsWith(".") || uri.endsWith(".")) {
            return null
        }

        // Convert to absolute path.
        return dirName + File.separator + uri
    }

    def sendError(ctx: ChannelHandlerContext, status: HttpResponseStatus) {
        val response: HttpResponse = new DefaultHttpResponse(HTTP_1_1, status)
        response.setHeader(CONTENT_TYPE, "text/plain charset=UTF-8")
        response.setContent(ChannelBuffers.copiedBuffer(
                "Failure: " + status.toString() + "\r\n",
                CharsetUtil.UTF_8))

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE)
    }
    
    def sendNotModified(ctx: ChannelHandlerContext) {
        val response: HttpResponse = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_MODIFIED)
        setDateHeader(response)

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE)
    }
    
    def setDateHeader(response: HttpResponse) {
        val dateFormatter: SimpleDateFormat = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US)
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE))

        val time: Calendar = new GregorianCalendar()
        response.setHeader(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()))
    }
    
    def setDateAndCacheHeaders(response: HttpResponse, fileToCache: File) {
        val dateFormatter: SimpleDateFormat = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US)
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE))

        // Date header
        val time: Calendar = new GregorianCalendar()
        response.setHeader(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()))

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS)
        response.setHeader(HttpHeaders.Names.EXPIRES, dateFormatter.format(time.getTime()))
        response.setHeader(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS)
        response.setHeader(HttpHeaders.Names.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())))
    }

    def setContentTypeHeader(response: HttpResponse, file: File) {
        val mimeTypesMap: MimetypesFileTypeMap = new MimetypesFileTypeMap()
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()))
    }

}