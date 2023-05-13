type Props = {
	isSearching: boolean;
	setIsSearching: React.Dispatch<React.SetStateAction<boolean>>;

	txt: string;
	setText: React.Dispatch<React.SetStateAction<string>>;
	setIsFocused: React.Dispatch<React.SetStateAction<boolean>>;
	inputRef: React.RefObject<HTMLInputElement>;
};
