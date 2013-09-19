package ch.spacebase.mcprotocol.standard.packet;

import ch.spacebase.mcprotocol.net.io.NetInput;
import ch.spacebase.mcprotocol.net.io.NetOutput;
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
		
//		System.out.println("PacketMapChunk::constructor => Input data has length "+data.length);
		//this.data = data;
		
		Deflater deflater = new Deflater(-1);
		this.data = new byte[0];
		this.length = 0;
		try {
			deflater.setInput(data, 0, data.length);
			deflater.finish();
			this.data = new byte[data.length];
			this.length = deflater.deflate(this.data);
	//		System.out.println("PacketMapChunk::constructor => Compressed data has length "+this.length);

		} finally {
			deflater.end();
		}
		
	}

	@Override
	public void read(NetInput in) throws IOException {
		this.x = in.readInt();
		this.z = in.readInt();
	//	System.out.print("PacketMapChunk::read => reading chunk ("+this.x+","+this.z+")");
		this.groundUp = in.readBoolean();
	//	System.out.print("PacketMapChunk::read => groundUp: "+this.groundUp);
		this.startY = in.readShort();
	//	System.out.print("PacketMapChunk::read => primaryBitMap: "+this.startY);
		this.endY = in.readShort();
		this.length = in.readInt();
	//	System.out.print("PacketMapChunk::read => : data length: "+this.length);


	//	System.out.print("PacketMapChunk::read => Attempting to read "+this.length+" bytes of compressed data."); 
		byte[] compressed = in.readBytes(this.length);

		int off = 0;
		int msb = 0;
		for(int count = 0; count < 16; count++) {
			off += this.startY >> count & 1;
			msb += this.endY >> count & 1;
		}

		int size = (12288 * off) + (2048 * msb);
		if(this.groundUp) {
			size += 256;
		}

		this.data = new byte[size];
		Inflater inflater = new Inflater();
		inflater.setInput(compressed, 0, this.length);
	//	System.out.print("PacketMapChunk::read => Data ready to be decompressed");


		try {
			int result = inflater.inflate(this.data);
		//	System.out.print("PacketMapChunk::read => Decompressed "+result+" byes of data");
		} catch (DataFormatException e) {
			throw new IOException("Bad compressed data format");
		} finally {
			inflater.end();
		}
	}

	@Override
	public void write(NetOutput out) throws IOException {
		out.writeInt(this.x);
		out.writeInt(this.z);
		out.writeBoolean(this.groundUp);
		out.writeShort((short) (this.startY & 0xffff));
		out.writeShort((short) (this.endY & 0xffff));
		out.writeInt(this.length);
		out.writeBytes(this.data, this.length);
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
