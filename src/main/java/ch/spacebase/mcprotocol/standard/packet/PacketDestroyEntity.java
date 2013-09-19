package ch.spacebase.mcprotocol.standard.packet;

import ch.spacebase.mcprotocol.net.io.NetInput;
import ch.spacebase.mcprotocol.net.io.NetOutput;
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
	public void read(NetInput in) throws IOException {
		this.entityIds = new int[in.readUnsignedByte()];
		for(int count = 0; count < this.entityIds.length; count++) {
			this.entityIds[count] = in.readInt();
			//System.out.print("PacketDestroyEntity::read => EntityID "+this.entityIds[count]+" should be destroyed");
		}
	}

	@Override
	public void write(NetOutput out) throws IOException {
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
