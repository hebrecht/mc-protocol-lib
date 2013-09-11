package ch.spacebase.mcprotocol.standard.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ch.spacebase.mcprotocol.net.Client;
import ch.spacebase.mcprotocol.net.ServerConnection;
import ch.spacebase.mcprotocol.packet.Packet;

public class PacketDestroyEntity extends Packet {

	public int entityIds[];

	public PacketDestroyEntity() {
	}

	public PacketDestroyEntity(int... entityIds) {
		this.entityIds = entityIds.clone();
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		int numEntityIds = in.readUnsignedByte();
		//System.out.print("PacketDestroyEntity::read => Read "+numEntityIds+" entityIDs to be destroyed");
		this.entityIds = new int[numEntityIds];
		for(int count = 0; count < this.entityIds.length; count++) {
			this.entityIds[count] = in.readInt();
			//System.out.print("PacketDestroyEntity::read => EntityID "+this.entityIds[count]+" should be destroyed");
		}
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeByte(this.entityIds.length);
		//System.out.print("PacketDestroyEntity::write => Writing "+this.entityIds.length+" entityIDs to be destroyed");
		for(int id : this.entityIds) {
			out.writeInt(id);
			//System.out.print("PacketDestroyEntity::write => EntityID "+id+" should be destroyed");
		}
	}

	@Override
	public void handleClient(Client conn) {
	}

	@Override
	public void handleServer(ServerConnection conn) {
	}

	@Override
	public int getId() {
		return 29;
	}

}
