import * as React from 'react';

export function InputText(
    {onSubmit}: { onSubmit: (text: string) => void }
): React.JSX.Element {
    const [text, setText] = React.useState<string>('')
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => setText(event.target.value)
    return (
        <div>
            <input type="text" value={text} onChange={handleChange}/>
            <button onClick={() => onSubmit(text)}>Submit</button>
        </div>
    )
}