# Define a mapping from each byte value to a unique emoji
EMOJI_MAP = [
            "😀", "😁", "😂", "😃", "😄", "😅", "😆", "😇", "😈", "👿", "😉", "😊", "😋", "😌", "😍", "😎",
            "😏", "😐", "😑", "😒", "😓", "😔", "😕", "😖", "😗", "😘", "😙", "😚", "😛", "😜", "😝", "😞",
            "😟", "😠", "😡", "😢", "😣", "😤", "😥", "😦", "😧", "😨", "😩", "😪", "😫", "😬", "😭", "😮",
            "😯", "😰", "😱", "😲", "😳", "😴", "😵", "😶", "😷", "😸", "😹", "😺", "😻", "😼", "😽", "😾",
            "😿", "🙀", "🙁", "🙂", "🙃", "🙄", "🙅", "🙆", "🙇", "🙈", "🙉", "🙊", "🙋", "🙌", "🙍", "🙎",
            "🙏", "🚀", "🚁", "🚂", "🚃", "🚄", "🚅", "🚆", "🚇", "🚈", "🚉", "🚊", "🚋", "🚌", "🚍", "🚎",
            "🚏", "🚐", "🚑", "🚒", "🚓", "🚔", "🚕", "🚖", "🚗", "🚘", "🚙", "🚚", "🚛", "🚜", "🚝", "🚞",
            "🚟", "🚠", "🚡", "🚢", "🚣", "🚤", "🚥", "🚦", "🚧", "🚨", "🚩", "🚪", "🚫", "🚬", "🚭", "🚮",
            "🚯", "🚰", "🚱", "🚲", "🚳", "🚴", "🚵", "🚶", "🚷", "🚸", "🚹", "🚺", "🚻", "🚼", "🚽", "🚾",
            "🚿", "🛀", "🛁", "🛂", "🛃", "🛄", "🛅", "🛋", "🛌", "🛍", "🛎", "🛏", "🛐", "🛑", "🛒", "🛠",
            "🛡", "🛢", "🛣", "🛤", "🛥", "🛩", "🛫", "🛬", "🛰", "🛳", "🛴", "🛵", "🛶", "🛷", "🛸", "🛹",
            "🛺", "🟠", "🟡", "🟢", "🟣", "🟤", "🟥", "🟧", "🟨", "🟩", "🟪", "🟫", "🤍", "🤎", "🤏", "🤐",
            "🤑", "🤒", "🤓", "🤔", "🤕", "🤖", "🤗", "🤘", "🤙", "🤚", "🤛", "🤜", "🤝", "🤞", "🤟", "🤠",
            "🤡", "🤢", "🤣", "🤤", "🤥", "🤦", "🤧", "🤨", "🤩", "🤪", "🤫", "🤬", "🤭", "🤮", "🤯", "🤰",
            "🤱", "🤲", "🤳", "🤴", "🤵", "🤶", "🤷", "🤸", "🤹", "🤺", "🤼", "🤽", "🤾", "🤿", "🥀", "🥁",
            "🥂", "🥃", "🥄", "🥅", "🥇", "🥈", "🥉", "🥊", "🥋", "🥌", "🥍", "🥎", "🥏", "🥐", "🥑", "🥥"
        ]
# Reverse mapping from emoji to byte value
EMOJI_TO_BYTE = {emoji: index for index, emoji in enumerate(EMOJI_MAP)}

def bytes_to_emoji(byte_data):
    # Convert a byte array to an array of emojis
    emoji_list = ''.join([EMOJI_MAP[byte] for byte in byte_data])
    return emoji_list

def emoji_to_bytes(emoji_list):
    # Convert an array of emojis back to a byte array
    # Check if all emojis are in the reverse mapping
    if not all(emoji in EMOJI_TO_BYTE for emoji in emoji_list):
        raise ValueError("One or more emojis are not in the defined mapping.")
    
    byte_array = bytes([EMOJI_TO_BYTE[emoji] for emoji in emoji_list])
    return byte_array