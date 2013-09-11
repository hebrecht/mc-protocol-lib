package ch.spacebase.mcprotocol.standard.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import ch.spacebase.mcprotocol.net.Client;
import ch.spacebase.mcprotocol.net.ServerConnection;
import ch.spacebase.mcprotocol.packet.Packet;

public class PacketMapChunk extends Packet {

	public int x;
	public int z;
	public boolean groundUp;
	public int startY;
	public int endY;
	public byte data[];
	public int length;

	public PacketMapChunk() {
	}

	public PacketMapChunk(int x, int z, boolean groundUp, int startY, int endY, byte data[]) {
		this.x = x;
		this.z = z;
		this.groundUp = groundUp;
		this.startY = startY;
		this.endY = endY;

		Deflater deflater = new Deflater(-1);

		try {
			deflater.setInput(data, 0, data.length);
			deflater.finish();
			this.data = new byte[data.length];
			this.length = deflater.deflate(this.data);
		} finally {
			deflater.end();
		}
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		this.x = in.readInt();
		this.z = in.readInt();
		//System.out.print("PacketMapChunk::read => reading chunk ("+this.x+","+this.z+")");
		this.groundUp = in.readBoolean();
		//System.out.print("PacketMapChunk::read => groundUp: "+this.groundUp);
		this.startY = in.readShort();
		//System.out.print("PacketMapChunk::read => primaryBitMap: "+this.startY);
		this.endY = in.readShort();
		//System.out.print("PacketMapChunk::read => addBitMap: "+this.endY);
		this.length = in.readInt();
		//System.out.print("PacketMapChunk::read => : data length: "+this.length);


		byte[] compressed = new byte[this.length];
		//System.out.print("PacketMapChunk::read => Attempting to read "+this.length+" bytes of compressed data.");	
		in.readFully(compressed);
		//System.out.print("PacketMapChunk::read => Read compressed data");

		int off = 0;
		for(int count = 0; count < 16; count++) {
			off += this.startY >> count & 1;
		}

		int size = 12288* off;
		if(this.groundUp) {
			size += 256;
		}

		this.data = new byte[size];
		Inflater inflater = new Inflater();
		inflater.setInput(compressed, 0, this.length);
		//System.out.print("PacketMapChunk::read => Data ready to be decompressed");
		
		try {
			int result = inflater.inflate(this.data);
			//System.out.print("PacketMapChunk::read => Decompressed "+result+" byes of data");
		} catch (DataFormatException e) {
			throw new IOException("Bad compressed data format");
		} finally {
			inflater.end();
		}
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(this.x);
		out.writeInt(this.z);
		out.writeBoolean(this.groundUp);
		out.writeShort((short) (this.startY & 0xffff));
		out.writeShort((short) (this.endY & 0xffff));
		out.writeInt(this.length);
		out.write(this.data, 0, this.length);
	}

	@Override
	public void handleClient(Client conn) {
	}

	@Override
	public void handleServer(ServerConnection conn) {
	}

	@Override
	public int getId() {
		return 51;
	}

}
