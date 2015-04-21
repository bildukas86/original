/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.datatables.PetDataTable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;

public final class RequestChangePetName extends L2GameClientPacket
{
	private String _name;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (!activeChar.hasPet())
			return;
		
		final L2PetInstance pet = (L2PetInstance) activeChar.getPet();
		
		if (pet.getName() != null)
		{
			activeChar.sendPacket(SystemMessageId.NAMING_YOU_CANNOT_SET_NAME_OF_THE_PET);
			return;
		}
		
		if (PetDataTable.getInstance().doesPetNameExist(_name, pet.getTemplate().getNpcId()))
		{
			activeChar.sendPacket(SystemMessageId.NAMING_ALREADY_IN_USE_BY_ANOTHER_PET);
			return;
		}
		
		if (_name.length() < 2 || _name.length() > 8)
		{
			activeChar.sendPacket(SystemMessageId.NAMING_PETNAME_UP_TO_8CHARS);
			return;
		}
		
		if (!StringUtil.isValidName(_name, "^[A-Za-z]{2,8}$"))
		{
			activeChar.sendPacket(SystemMessageId.NAMING_PETNAME_CONTAINS_INVALID_CHARS);
			return;
		}
		
		pet.setName(_name);
		pet.sendPetInfosToOwner();
	}
}